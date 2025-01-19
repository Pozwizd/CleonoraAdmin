package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import com.example.cleonoraadmin.mapper.OrderCleaningMapper;
import com.example.cleonoraadmin.mapper.OrderMapper;
import com.example.cleonoraadmin.model.SalesChartDataDTO;
import com.example.cleonoraadmin.model.SalesChartProjection;
import com.example.cleonoraadmin.model.TopCleaningDTO;
import com.example.cleonoraadmin.model.TopCleaningProjection;
import com.example.cleonoraadmin.model.order.CustomerAddressRequest;
import com.example.cleonoraadmin.model.order.OrderCleaningRequest;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.repository.OrderRepository;
import com.example.cleonoraadmin.repository.TimeSlotRepository;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.service.CustomerService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImp implements OrderService {

    private final CustomerService customerService;
    private final OrderMapper orderMapper;
    private final OrderCleaningMapper orderCleaningMapper;
    private final GeoapifyServiceImp geoapifyService;
    private final OrderCleaningSchedulingServiceImp
            orderCleaningSchedulingServiceImp;
    private final CleaningService cleaningService;



    private final OrderRepository orderRepository;
    private final TimeSlotRepository timeSlotRepository;



    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {

        Customer customer = customerService.getCustomerById(orderRequest.getCustomerId()).orElseThrow();

        Order order = orderMapper.toEntity(orderRequest);
        order.setCustomer(customer);
        order.setAddressOrder(geoapifyService.processAddress((CustomerAddressRequest) orderRequest.getAddress()));

        for (OrderCleaningRequest orderCleaningRequest : orderRequest.getOrderCleanings()) {
            order.addOrderCleaning(orderCleaningMapper.toEntity(
                    orderCleaningRequest, cleaningService));
        }

        orderCleaningSchedulingServiceImp.createTimeSlotsForOrder(order);

        order.setEndDate(orderCleaningSchedulingServiceImp.calculateEndDate(order));

        return orderMapper.toResponse(orderRepository.save(order), orderCleaningMapper);
    }

    @Transactional
    @Override
    public OrderResponse updateOrder(OrderRequest updatedOrderRequest) {
        Order existingOrder = orderRepository.findById(updatedOrderRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + updatedOrderRequest.getId()));

        timeSlotRepository.deleteAll(existingOrder.getOrderCleanings()
                .stream().flatMap(orderCleaning -> orderCleaning.getTimeSlots().stream()).toList());
        existingOrder.getOrderCleanings().clear();

        for (OrderCleaningRequest orderCleaningRequest : updatedOrderRequest.getOrderCleanings()) {
            existingOrder.addOrderCleaning(orderCleaningMapper.toEntity(
                    orderCleaningRequest, cleaningService));
        }

        existingOrder.setAddressOrder(geoapifyService.processAddress((CustomerAddressRequest) updatedOrderRequest.getAddress()));
        orderCleaningSchedulingServiceImp.createTimeSlotsForOrder(existingOrder);

        existingOrder.setEndDate(orderCleaningSchedulingServiceImp.calculateEndDate(existingOrder));

        return orderMapper.toResponse(orderRepository.save(existingOrder), orderCleaningMapper);
    }

    @Override
    public Page<OrderResponse> getPageAllOrders(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.searchByCustomerField(search), pageRequest), orderCleaningMapper);
    }

    @Override
    public Page<OrderResponse> getPageAllOrdersLastWeek(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.updatedWithinLastWeek(), pageRequest), orderCleaningMapper);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order, orderCleaningMapper);
    }

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

    @Override
    public BigDecimal calculateSalesLastWeek() {
        return orderRepository.findAll(OrderSpecification.updatedWithinLastWeek().and(OrderSpecification.byStatus(OrderStatus.COMPLETED))).stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean ifOrderMoreThan(int i) {
        return orderRepository.count() > i;
    }

    @Override
    public Integer countCompletedOrders() {
        return orderRepository.countCompletedOrders();
    }

    @Override
    public Integer countDailyCompletedOrders() {
        return orderRepository.findAll(OrderSpecification.orderUpdateLastDaily().and(OrderSpecification.byStatus(OrderStatus.COMPLETED))).size();
    }



    @Override
    public BigDecimal calculateTotalSales() {
        return orderRepository.findAll(OrderSpecification.byStatus(OrderStatus.COMPLETED)).stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

        Map<String, List<Long>> datasets = new HashMap<>();
        topCleaningNames.forEach(name -> datasets.put(name, new ArrayList<>(Collections.nCopies(months, 0L))));

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
