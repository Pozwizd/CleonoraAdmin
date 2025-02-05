package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.config.WorkScheduleConfig;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderCleaning;
import com.example.cleonoraadmin.entity.TimeSlot;
import com.example.cleonoraadmin.entity.Workday;
import com.example.cleonoraadmin.repository.OrderCleaningRepository;
import com.example.cleonoraadmin.repository.TimeSlotRepository;
import com.example.cleonoraadmin.repository.WorkdayRepository;
import com.example.cleonoraadmin.service.OrderCleaningSchedulingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@Slf4j
public class OrderCleaningSchedulingServiceImp implements OrderCleaningSchedulingService {

    private final WorkScheduleConfig workScheduleConfig;
    private final WorkdayRepository workdayRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final com.example.cleonoraadmin.repository.OrderCleaningRepository orderCleaningRepository;

    public OrderCleaningSchedulingServiceImp(WorkScheduleConfig workScheduleConfig, WorkdayRepository workdayRepository, TimeSlotRepository timeSlotRepository, OrderCleaningRepository orderCleaningRepository) {
        this.workScheduleConfig = workScheduleConfig;
        this.workdayRepository = workdayRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.orderCleaningRepository = orderCleaningRepository;
    }

    @Override
    @Transactional
    public void createTimeSlotsForOrder(Order order) {
        for (OrderCleaning orderCleaning : order.getOrderCleanings()) {
            orderCleaning = this.orderCleaningRepository.save(orderCleaning);
            LocalDate currentDate = order.getStartDate();
            LocalTime currentTime = order.getStartTime();
            Duration remainingDuration = orderCleaning.getDurationCleaning();


            while (remainingDuration.toMinutes() > 0) {

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

            orderCleaningRepository.save(orderCleaning);
        }
    }

    public LocalTime findEndTime(LocalDate date, LocalTime startTime, LocalTime potentialEndTime, LocalTime workdayEnd) {
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


    public Workday getOrCreateWorkday(LocalDate date) {
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

    @Override
    public LocalDate calculateEndDate(Order order) {
        return order.getOrderCleanings().stream()
                .flatMap(oc -> oc.getTimeSlots().stream())
                .map(TimeSlot::getDate)
                .max(LocalDate::compareTo)
                .orElse(order.getStartDate());
    }

    @Transactional
    public void deleteOrderCleanings(Order existingOrder) {
        orderCleaningRepository.deleteAll(existingOrder.getOrderCleanings());
    }
}
