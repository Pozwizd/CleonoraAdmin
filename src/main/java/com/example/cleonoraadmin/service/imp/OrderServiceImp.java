package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.config.WorkScheduleConfig;
import com.example.cleonoraadmin.entity.*;
import com.example.cleonoraadmin.mapper.OrderCleaningMapper;
import com.example.cleonoraadmin.mapper.OrderMapper;
import com.example.cleonoraadmin.model.*;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.repository.*;
import com.example.cleonoraadmin.service.OrderCleaningService;
import com.example.cleonoraadmin.service.OrderService;
import com.example.cleonoraadmin.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {

    private final WorkScheduleConfig workScheduleConfig;
    private final OrderRepository orderRepository;
    private final CleaningRepository cleaningRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final OrderCleaningMapper orderCleaningMapper;
    private final WorkdayRepository workdayRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final OrderCleaningService orderCleaningService;
    private final OrderCleaningRepository orderCleaningRepository;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toEntity(orderRequest, customerRepository, orderCleaningMapper, cleaningRepository);

        createTimeSlotsForOrder(order);

        order.calculatePrice();
        order.setEndDate(calculateEndDate(order));
        return orderMapper.toResponse(orderRepository.save(order), orderCleaningMapper);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(OrderRequest updatedOrderRequest) {
        Order existingOrder = orderRepository.findById(updatedOrderRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + updatedOrderRequest.getId()));
        Order newOrder = orderMapper.toEntity(updatedOrderRequest, customerRepository, orderCleaningMapper, cleaningRepository);

        timeSlotRepository.deleteAll(existingOrder.getOrderCleanings()
                .stream().flatMap(orderCleaning -> orderCleaning.getTimeSlots().stream()).toList());
        orderCleaningRepository.deleteAll(existingOrder.getOrderCleanings());
        existingOrder.getOrderCleanings().clear();
        orderRepository.save(existingOrder);

        createTimeSlotsForOrder(newOrder);

        newOrder.calculatePrice();
        newOrder.setEndDate(calculateEndDate(newOrder));

        return orderMapper.toResponse(orderRepository.save(newOrder), orderCleaningMapper);
    }



    private void createTimeSlotsForOrder(Order order) {
        for (OrderCleaning orderCleaning : order.getOrderCleanings()) {
            orderCleaning = orderCleaningRepository.save(orderCleaning);
            LocalDate currentDate = order.getStartDate();
            LocalTime currentTime = order.getStartTime();
            Duration remainingDuration = orderCleaning.getDurationCleaning();

            int maxIterations = 1000;
            int iteration = 0;

            while (remainingDuration.toMinutes() > 0 && iteration < maxIterations) {
                iteration++;
                log.debug("Iteration {}: Date={}, Time={}, RemainingDuration={}", iteration, currentDate, currentTime, remainingDuration);

                String dayOfWeek = currentDate.getDayOfWeek().toString().toLowerCase();
                WorkScheduleConfig.WorkDay workDay = workScheduleConfig.getSchedule().get(dayOfWeek);

                if (isWeekend(workDay)) {
                    currentDate = getNextWorkingDay(currentDate);
                    currentTime = getStartTimeForDay(currentDate);
                    continue;
                }

                LocalTime workdayStart = LocalTime.parse(workDay.getStart());
                LocalTime workdayEnd = LocalTime.parse(workDay.getEnd());
                LocalTime lunchStart = LocalTime.parse(workDay.getLunch().getStart());
                LocalTime lunchEnd = LocalTime.parse(workDay.getLunch().getEnd());

                if (currentTime.isBefore(workdayStart)) {
                    currentTime = workdayStart;
                }

                LocalTime slotEndTime;

                if (currentTime.isBefore(lunchStart)) {
                    LocalTime potentialEndTime = currentTime.plus(remainingDuration);
                    if (potentialEndTime.isAfter(lunchStart)) {
                        slotEndTime = lunchStart;
                    } else {
                        slotEndTime = findEndTime(currentDate, currentTime, potentialEndTime, workdayEnd);
                    }
                } else if (currentTime.compareTo(lunchEnd) >= 0 && currentTime.isBefore(workdayEnd)) {
                    LocalTime potentialEndTime = currentTime.plus(remainingDuration);
                    slotEndTime = findEndTime(currentDate, currentTime, potentialEndTime, workdayEnd);
                } else {
                    currentDate = getNextWorkingDay(currentDate);
                    currentTime = getStartTimeForDay(currentDate);
                    continue;
                }

                if (slotEndTime.isBefore(currentTime) || slotEndTime.equals(currentTime)) {
                    log.error("Invalid slotEndTime: {} <= currentTime: {}", slotEndTime, currentTime);
                    break;
                }

                TimeSlot timeSlot = createTimeSlot(orderCleaning, currentDate, currentTime, slotEndTime);
                orderCleaning.addTimeSlot(timeSlot);

                Duration slotDuration = Duration.between(currentTime, slotEndTime);
                if (slotDuration.isNegative() || slotDuration.isZero()) {
                    log.error("Invalid slot duration: {}", slotDuration);
                    break;
                }

                remainingDuration = remainingDuration.minus(slotDuration);
                currentTime = slotEndTime;

                if (currentTime.equals(lunchStart)) {
                    currentTime = lunchEnd;
                }

                if (currentTime.equals(workdayEnd)) {
                    currentDate = getNextWorkingDay(currentDate);
                    currentTime = getStartTimeForDay(currentDate);
                }
            }

            if (iteration >= maxIterations) {
                log.error("Reached maximum iterations while creating time slots for orderCleaning: {}", orderCleaning.getId());
                throw new RuntimeException("Exceeded maximum iterations while creating time slots.");
            }

            orderCleaningRepository.save(orderCleaning);
        }
    }


    private LocalTime findEndTime(LocalDate date, LocalTime startTime, LocalTime potentialEndTime, LocalTime workdayEnd) {
        Optional<TimeSlot> overlappingSlot = timeSlotRepository.findFirstByDateAndStartTimeLessThanAndEndTimeGreaterThan(date, potentialEndTime, startTime);
        if (overlappingSlot.isPresent()) {
            return overlappingSlot.get().getStartTime();
        } else {
            return potentialEndTime.isBefore(workdayEnd) ? potentialEndTime : workdayEnd;
        }
    }

    private TimeSlot createTimeSlot(OrderCleaning orderCleaning, LocalDate date, LocalTime startTime, LocalTime endTime) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(date);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setOrderCleaning(orderCleaning);
        timeSlot.setWorkday(getOrCreateWorkday(date));
        return timeSlotRepository.save(timeSlot);
    }



    private Workday getOrCreateWorkday(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
        WorkScheduleConfig.WorkDay workDay = workScheduleConfig.getSchedule().get(dayOfWeek);

        if (workDay == null || "off".equalsIgnoreCase(workDay.getStart())) {
            log.info("Attempted to create a workday for a day off: {}", dayOfWeek);
            return null;
        }

        LocalTime workdayStart = LocalTime.parse(workDay.getStart());
        LocalTime workdayEnd = LocalTime.parse(workDay.getEnd());
        LocalTime lunchStart = LocalTime.parse(workDay.getLunch().getStart());
        LocalTime lunchEnd = LocalTime.parse(workDay.getLunch().getEnd());

        return workdayRepository.findByDate(date)
                .orElseGet(() -> {
                    Workday newWorkday = new Workday();
                    newWorkday.setDate(date);
                    newWorkday.setStartTime(workdayStart);
                    newWorkday.setEndTime(workdayEnd);
                    newWorkday.setLunchStart(lunchStart);
                    newWorkday.setLunchEnd(lunchEnd);
                    return workdayRepository.save(newWorkday);
                });
    }

    private boolean isWeekend(WorkScheduleConfig.WorkDay workDay) {
        return workDay == null || "off".equalsIgnoreCase(workDay.getStart());
    }

    private LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (isWeekend(workScheduleConfig.getSchedule().get(nextDay.getDayOfWeek().toString().toLowerCase()))) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }


    private LocalTime getStartTimeForDay(LocalDate date) {
        WorkScheduleConfig.WorkDay workDay = workScheduleConfig.getSchedule().get(date.getDayOfWeek().toString().toLowerCase());
        return LocalTime.parse(workDay.getStart());
    }

    private LocalDate calculateEndDate(Order order) {
        return order.getOrderCleanings().stream()
                .flatMap(oc -> oc.getTimeSlots().stream())
                .map(TimeSlot::getDate)
                .max(LocalDate::compareTo)
                .orElse(order.getStartDate());
    }

    private Workday createWorkday(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();

        WorkScheduleConfig.WorkDay workDay = workScheduleConfig.getSchedule().get(dayOfWeek);

        if (workDay == null || "off".equalsIgnoreCase(workDay.getStart())) {

            log.info("Попытка создать рабочий день для выходного: {}", dayOfWeek);
            return null;
        }

        LocalTime workdayStart = LocalTime.parse(workDay.getStart());
        LocalTime workdayEnd = LocalTime.parse(workDay.getEnd());

        Optional<Workday> existingWorkday = workdayRepository.findByDate(date);

        if (existingWorkday.isEmpty()) {
            Workday newWorkday = new Workday();
            newWorkday.setDate(date);
            newWorkday.setStartTime(workdayStart);
            newWorkday.setEndTime(workdayEnd);

            return workdayRepository.save(newWorkday);
        }

        return existingWorkday.get();
    }

    @Override
    public Page<OrderResponse> getPageAllOrders(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));

        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.searchByCustomerField(search),pageRequest),
                orderCleaningMapper);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toResponse(Objects.requireNonNull(orderRepository.findById(id).orElse(null)), orderCleaningMapper);
    }

    /**
     * @param id
     */
    @Override
    public boolean deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении заказа: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * @param i
     * @return
     */
    @Override
    public boolean ifOrderMoreThan(int i) {
        return orderRepository.count() > i;
    }

    /**
     * @return
     */
    @Override
    public Integer countCompletedOrders() {
        return orderRepository.countCompletedOrders();
    }

    /**
     * @return
     */
    @Override
    public Integer countDailyCompletedOrders() {
        return orderRepository.findAll(OrderSpecification.orderUpdateLastDaily()).size();
    }


    @Override
    public List<TopCleaningDTO> getTopCleanings(int topN, int months) {
        YearMonth startYearMonth = YearMonth.now().minusMonths(months - 1);
        LocalDate startDate = startYearMonth.atDay(1);

        List<TopCleaningProjection> projections = orderRepository.findTopCleanings(startDate, PageRequest.of(0, topN));

        Long total = projections.stream().mapToLong(TopCleaningProjection::getCount).sum();

        return projections.stream()
                .map(p -> {
                    TopCleaningDTO dto = new TopCleaningDTO();
                    dto.setName(p.getName());
                    dto.setCount(p.getCount());
                    dto.setPercentage(total > 0 ? (p.getCount() * 100.0) / total : 0.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    @Override
    public SalesChartDataDTO getSalesChartData(int topN, int months) {

        YearMonth currentYearMonth = YearMonth.now();

        YearMonth startYearMonth = currentYearMonth.minusMonths(months - 1);
        LocalDate startDate = startYearMonth.atDay(1);

        List<TopCleaningProjection> topCleanings = orderRepository.findTopCleanings(startDate, PageRequest.of(0, topN));

        List<SalesChartProjection> salesData = orderRepository.findSalesDataByMonth(startDate);

        Set<String> topCleaningNames = topCleanings.stream()
                .map(TopCleaningProjection::getName)
                .collect(Collectors.toSet());

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            YearMonth ym = startYearMonth.plusMonths(i);
            labels.add(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        }

        // Инициализация структуры данных для графика
        Map<String, List<Long>> datasets = new HashMap<>();
        topCleaningNames.forEach(name -> datasets.put(name, new ArrayList<>(Collections.nCopies(months, 0L))));

        // Заполнение данных
        for (SalesChartProjection data : salesData) {
            String name = data.getName();
            if (topCleaningNames.contains(name)) {
                YearMonth orderMonth = YearMonth.of(data.getYear().intValue(), data.getMonth().intValue());
                long monthsBetween = startYearMonth.until(orderMonth, ChronoUnit.MONTHS);
                int index = (int) monthsBetween;
                if (index >= 0 && index < months) {
                    datasets.get(name).set(index, data.getCount());
                }
            }
        }

        SalesChartDataDTO chartData = new SalesChartDataDTO();
        chartData.setLabels(labels);
        chartData.setDatasets(datasets);

        return chartData;
    }

}