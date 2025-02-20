package com.example.cleonoraadmin.service.imp;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.example.cleonoraadmin.config.WorkScheduleConfig;
import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderCleaning;
import com.example.cleonoraadmin.entity.OrderStatus;
import com.example.cleonoraadmin.mapper.OrderCleaningMapper;
import com.example.cleonoraadmin.mapper.OrderMapper;
import com.example.cleonoraadmin.model.SalesChartDataDTO;
import com.example.cleonoraadmin.model.SalesChartProjection;
import com.example.cleonoraadmin.model.TopCleaningDTO;
import com.example.cleonoraadmin.model.TopCleaningProjection;
import com.example.cleonoraadmin.model.order.CustomerAddressRequest;
import com.example.cleonoraadmin.model.order.OrderCleaningRequest;
import com.example.cleonoraadmin.model.order.OrderCleaningResponse;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.repository.OrderRepository;
import com.example.cleonoraadmin.repository.TimeSlotRepository;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.service.CustomerService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CleaningService cleaningService;
    @Mock
    private CustomerService customerService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderCleaningMapper orderCleaningMapper;
    @Mock
    private GeoapifyServiceImp geoapifyService;
    @Mock
    private OrderCleaningSchedulingServiceImp orderCleaningSchedulingServiceImp;
    @Mock
    private TimeSlotRepository timeSlotRepository;


    private OrderServiceImp orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImp(customerService,
                orderMapper,
                orderCleaningMapper,
                geoapifyService,
                orderCleaningSchedulingServiceImp,
                cleaningService,
                orderRepository,
                timeSlotRepository);
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        com.example.cleonoraadmin.model.order.OrderCleaningRequest orderCleaningRequest = new com.example.cleonoraadmin.model.order.OrderCleaningRequest();
        orderRequest.setOrderCleanings(List.of(orderCleaningRequest));
        CustomerAddressRequest customerAddressRequest = mock(CustomerAddressRequest.class);
        orderRequest.setAddress(customerAddressRequest);
        Order order = new Order();
        OrderResponse orderResponse = new OrderResponse();
        String email = "test@example.com";

        Customer mockCustomer = new Customer(); // Create a mock Customer
        when(customerService.getCustomerById(1L)).thenReturn(java.util.Optional.of(mockCustomer)); // Mock customerService
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(geoapifyService.processAddress(any())).thenReturn(null); // Mock to return null for simplicity
        doNothing().when(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(order);
        when(orderCleaningSchedulingServiceImp.calculateEndDate(order)).thenReturn(null); // Mock to return null
        com.example.cleonoraadmin.entity.OrderCleaning orderCleaning = new com.example.cleonoraadmin.entity.OrderCleaning();
        orderCleaning.setPrice(BigDecimal.TEN);
        when(orderCleaningMapper.toEntity(org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.service.CleaningService.class))).thenReturn(orderCleaning);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order, orderCleaningMapper)).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        verify(orderMapper).toEntity(orderRequest);
        verify(geoapifyService).processAddress(any());
        verify(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(order);
        verify(orderCleaningSchedulingServiceImp).calculateEndDate(order);
        verify(orderRepository).save(order);
        Assertions.assertNotNull(result);
    }

    @Test
    void updateOrder_shouldUpdateOrderSuccessfully() {

        OrderRequest updatedOrderRequest = new OrderRequest();
        com.example.cleonoraadmin.model.order.OrderCleaningRequest orderCleaningRequest = new com.example.cleonoraadmin.model.order.OrderCleaningRequest();
        updatedOrderRequest.setOrderCleanings(List.of(orderCleaningRequest));
        CustomerAddressRequest customerAddressRequest2 = mock(CustomerAddressRequest.class);
        updatedOrderRequest.setAddress(customerAddressRequest2);
        updatedOrderRequest.setId(1L);
        Order existingOrder = new Order();
        OrderResponse orderResponse = new OrderResponse();
        String username = "testuser";

        when(orderRepository.findById(updatedOrderRequest.getId())).thenReturn(java.util.Optional.of(existingOrder));
        when(geoapifyService.processAddress(any())).thenReturn(null);
        doNothing().when(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(existingOrder);
        com.example.cleonoraadmin.entity.OrderCleaning orderCleaning = new com.example.cleonoraadmin.entity.OrderCleaning();
        orderCleaning.setPrice(BigDecimal.TEN);
        when(orderCleaningMapper.toEntity(org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.service.CleaningService.class))).thenReturn(orderCleaning);
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);
        when(orderMapper.toResponse(existingOrder, orderCleaningMapper)).thenReturn(orderResponse);

        OrderResponse result = orderService.updateOrder(updatedOrderRequest);

        verify(orderRepository).findById(updatedOrderRequest.getId());
        verify(geoapifyService).processAddress(any());
        verify(timeSlotRepository).deleteAll(anyList());
        verify(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(existingOrder);
        verify(orderCleaningMapper).toEntity(org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleonoraadmin.service.CleaningService.class));
        verify(orderRepository).save(existingOrder);
        verify(orderMapper).toResponse(existingOrder, orderCleaningMapper);

    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        Long orderId = 1L;
        Order order = new Order();
        OrderResponse orderResponse = new OrderResponse();

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toResponse(order, orderCleaningMapper)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderById(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderMapper).toResponse(order, orderCleaningMapper);
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderDoesNotExist() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        verify(orderRepository).findById(orderId);
    }

    @Test
    void deleteOrder_shouldReturnTrue_whenDeletionIsSuccessful() {
        Long orderId = 1L;
        doNothing().when(orderRepository).deleteById(orderId);

        boolean result = orderService.deleteOrder(orderId);

        verify(orderRepository).deleteById(orderId);
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }

    @Test
    void deleteOrder_shouldReturnFalse_whenDeletionFails() {
        Long orderId = 1L;
        doThrow(new RuntimeException("Deletion failed")).when(orderRepository).deleteById(orderId);

        boolean result = orderService.deleteOrder(orderId);

        verify(orderRepository).deleteById(orderId);
        org.junit.jupiter.api.Assertions.assertFalse(result);
    }

    @Test
    void getPageAllOrders_shouldReturnPageOfOrders() {
        int page = 0;
        int size = 10;
        String search = "";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(List.of(new Order()), pageRequest, 1);
        Page<OrderResponse> orderResponsePage = new org.springframework.data.domain.PageImpl<>(List.of(new OrderResponse()), pageRequest, 1);


        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        Page<OrderResponse> result = orderService.getPageAllOrders(page, size, search);

        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest));
        verify(orderMapper).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void getPageAllOrdersLastWeek_shouldReturnPageOfOrders() {
        int page = 0;
        int size = 10;
        String search = "";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(List.of(new Order()), pageRequest, 1);
        Page<OrderResponse> orderResponsePage = new org.springframework.data.domain.PageImpl<>(List.of(new OrderResponse()), pageRequest, 1);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        Page<OrderResponse> result = orderService.getPageAllOrdersLastWeek(page, size, search);

        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest));
        verify(orderMapper).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void calculateSalesLastWeek_shouldReturnCorrectSalesAmount() {
        Order order1 = new Order();
        order1.setPrice(BigDecimal.valueOf(100));
        Order order2 = new Order();
        order2.setPrice(BigDecimal.valueOf(200));
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(orders);

        BigDecimal result = orderService.calculateSalesLastWeek();

        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.valueOf(300), result);
    }

    @Test
    void ifOrderMoreThan_shouldReturnTrue_whenOrderCountIsGreaterThanGivenValue() {
        // Arrange
        int i = 5;
        when(orderRepository.count()).thenReturn(6L);
        // Act
        boolean result = orderService.ifOrderMoreThan(i);

        // Assert
        verify(orderRepository).count();
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }

    @Test
    void ifOrderMoreThan_shouldReturnFalse_whenOrderCountIsLessThanGivenValue() {
        int i = 5;
        when(orderRepository.count()).thenReturn(4L);

        boolean result = orderService.ifOrderMoreThan(i);

        verify(orderRepository).count();
        org.junit.jupiter.api.Assertions.assertFalse(result);
    }

    @Test
    void countCompletedOrders_shouldReturnCorrectCount() {
        when(orderRepository.countCompletedOrders()).thenReturn(5);

        Integer result = orderService.countCompletedOrders();

        verify(orderRepository).countCompletedOrders();
        org.junit.jupiter.api.Assertions.assertEquals(5, result);
    }

    @Test
    void countDailyCompletedOrders_shouldReturnCorrectCount() {
        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(new Order(), new Order()));

        Integer result = orderService.countDailyCompletedOrders();

        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(2, result);
    }

    @Test
    void calculateTotalSales_shouldReturnCorrectAmount() {
        Order order1 = new Order();
        order1.setPrice(BigDecimal.valueOf(100));
        Order order2 = new Order();
        order2.setPrice(BigDecimal.valueOf(200));
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(orders);

        BigDecimal result = orderService.calculateTotalSales();

        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.valueOf(300), result);
    }

    @Test
    void getTopCleanings_shouldReturnCorrectData() {
        int topN = 3;
        int months = 6;
        YearMonth startYearMonth = YearMonth.now().minusMonths(months - 1);
        LocalDate startDate = startYearMonth.atDay(1);

        List<TopCleaningProjection> projections = List.of(
                createMockProjection("Cleaning A", 100L),
                createMockProjection("Cleaning B", 50L),
                createMockProjection("Cleaning C", 25L)
        );

        when(orderRepository.findTopCleanings(eq(startDate), any(PageRequest.class))).thenReturn(projections);

        List<TopCleaningDTO> result = orderService.getTopCleanings(topN, months);

        verify(orderRepository).findTopCleanings(eq(startDate), any(PageRequest.class));
        Assertions.assertEquals(topN, result.size());
        Assertions.assertEquals("Cleaning A", result.get(0).getName());
        Assertions.assertEquals(100L, result.get(0).getCount());
        Assertions.assertEquals(57.14, Math.round(result.get(0).getPercentage() * 100.0) / 100.0);

    }

    private TopCleaningProjection createMockProjection(String name, Long count) {
        return new TopCleaningProjection() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Long getCount() {
                return count;
            }

            @Override
            public String toString() {
                return "TopCleaningProjection{" +
                        "name='" + getName() + '\'' +
                        ", count=" + count +
                        '}';
            }
        };
    }



    private SalesChartProjection createMockSalesData(String name, Integer year, Integer month, Long count) {
        return new SalesChartProjection() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Long getCount() {
                return count;
            }

            @Override
            public Integer getYear() {
                return year;
            }

            @Override
            public Integer getMonth() {
                return month;
            }

            @Override
            public String toString() {
                return "SalesChartProjection{" +
                        "name='" + getName() + '\'' +
                        ", year=" + getYear() +
                        ", month=" + getMonth() +
                        ", count=" + getCount() +
                        '}';
            }
        };
    }

//    ______


    @Test
    void updateOrder_OrderNotFound_ShouldThrowEntityNotFoundException() {
        OrderRequest updatedOrderRequest = createOrderRequest();
        updatedOrderRequest.setId(1L);

        when(orderRepository.findById(updatedOrderRequest.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(updatedOrderRequest));

        verify(orderRepository, times(1)).findById(updatedOrderRequest.getId());
        verifyNoMoreInteractions(orderMapper);
        verifyNoMoreInteractions(orderRepository);
    }


    @Test
    void getPageAllOrders_WithSearch_ShouldReturnPageOfOrderResponses() {
        int page = 0;
        int size = 10;
        String search = "Customer Name";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new PageImpl<>(List.of(createOrder(1L), createOrder(2L)), pageRequest, 2);
        Page<OrderResponse> orderResponsePage = new PageImpl<>(List.of(createOrderResponse(1L), createOrderResponse(2L)), pageRequest, 2);

        when(orderRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        Page<OrderResponse> resultPage = orderService.getPageAllOrders(page, size, search);

        assertNotNull(resultPage);
        assertEquals(orderResponsePage, resultPage);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(orderMapper, times(1)).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void getPageAllOrders_WithoutSearch_ShouldReturnPageOfOrderResponses() {
        int page = 0;
        int size = 10;
        String search = null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new PageImpl<>(List.of(createOrder(1L), createOrder(2L)), pageRequest, 2);
        Page<OrderResponse> orderResponsePage = new PageImpl<>(List.of(createOrderResponse(1L), createOrderResponse(2L)), pageRequest, 2);

        when(orderRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        Page<OrderResponse> resultPage = orderService.getPageAllOrders(page, size, search);

        assertNotNull(resultPage);
        assertEquals(orderResponsePage, resultPage);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(orderMapper, times(1)).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void getPageAllOrdersLastWeek_ShouldReturnPageOfOrderResponses() {
        int page = 0;
        int size = 10;
        String search = null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new PageImpl<>(List.of(createOrder(1L), createOrder(2L)), pageRequest, 2);
        Page<OrderResponse> orderResponsePage = new PageImpl<>(List.of(createOrderResponse(1L), createOrderResponse(2L)), pageRequest, 2);

        when(orderRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        Page<OrderResponse> resultPage = orderService.getPageAllOrdersLastWeek(page, size, search);

        assertNotNull(resultPage);
        assertEquals(orderResponsePage, resultPage);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(orderMapper, times(1)).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void getOrderById_OrderExists_ShouldReturnOrderResponse() {
        Long orderId = 1L;
        Order order = createOrder(orderId);
        OrderResponse orderResponse = createOrderResponse(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order, orderCleaningMapper)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderResponse, result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toResponse(order, orderCleaningMapper);
    }


    @Test
    void deleteOrder_SuccessfulDelete_ShouldReturnTrue() {
        Long orderId = 1L;
        doNothing().when(orderRepository).deleteById(orderId);

        boolean result = orderService.deleteOrder(orderId);

        assertTrue(result);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void deleteOrder_ExceptionDuringDelete_ShouldReturnFalse() {
        Long orderId = 1L;
        doThrow(new RuntimeException("Database error")).when(orderRepository).deleteById(orderId);

        boolean result = orderService.deleteOrder(orderId);

        assertFalse(result);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void calculateSalesLastWeek_ShouldReturnSalesAmount() {
        BigDecimal expectedSales = BigDecimal.valueOf(100.00);
        Order order = createOrder();
        order.setPrice(expectedSales);
        when(orderRepository.findAll(Mockito.any(Specification.class))).thenReturn(Collections.singletonList(order));

        BigDecimal result = orderService.calculateSalesLastWeek();

        assertEquals(expectedSales, result);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void ifOrderMoreThan_CountMoreThanI_ShouldReturnTrue() {
        int i = 5;
        when(orderRepository.count()).thenReturn(10L);

        boolean result = orderService.ifOrderMoreThan(i);

        assertTrue(result);
        verify(orderRepository, times(1)).count();
    }

    @Test
    void ifOrderMoreThan_CountLessThanOrEqualI_ShouldReturnFalse() {
        int i = 10;
        when(orderRepository.count()).thenReturn(5L);

        boolean result = orderService.ifOrderMoreThan(i);

        assertFalse(result);
        verify(orderRepository, times(1)).count();
    }

    @Test
    void countCompletedOrders_ShouldReturnCompletedOrdersCount() {
        int expectedCount = 15;
        when(orderRepository.countCompletedOrders()).thenReturn(expectedCount);

        Integer result = orderService.countCompletedOrders();

        assertEquals(expectedCount, result);
        verify(orderRepository, times(1)).countCompletedOrders();
    }

    @Test
    void countDailyCompletedOrders_ShouldReturnDailyCompletedOrdersCount() {
        int expectedCount = 5;
        List<Order> completedOrders = Arrays.asList(createOrder(), createOrder(), createOrder(), createOrder(), createOrder());
        when(orderRepository.findAll(Mockito.any(Specification.class))).thenReturn(completedOrders);

        Integer result = orderService.countDailyCompletedOrders();

        assertEquals(expectedCount, result);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void getTopCleanings_ShouldReturnTopCleaningDTOList() {
        int topN = 3;
        int months = 6;
        List<TopCleaningProjection> projections = Arrays.asList(
                createTopCleaningProjection("Cleaning 1", 10L),
                createTopCleaningProjection("Cleaning 2", 8L),
                createTopCleaningProjection("Cleaning 3", 5L)
        );
        when(orderRepository.findTopCleanings(Mockito.any(), Mockito.any())).thenReturn(projections);

        List<TopCleaningDTO> result = orderService.getTopCleanings(topN, months);

        assertNotNull(result);
        assertEquals(topN, result.size());
        verify(orderRepository, times(1)).findTopCleanings(Mockito.any(), Mockito.any());
    }

    @Test
    void getSalesChartData_ShouldReturnSalesChartDataDTO() {
        int topN = 3;
        int months = 6;

        // Топ-уборки
        List<TopCleaningProjection> topCleaningsProjections = Arrays.asList(
                createTopCleaningProjection("Cleaning 1", 10L),
                createTopCleaningProjection("Cleaning 2", 8L),
                createTopCleaningProjection("Cleaning 3", 5L)
        );

        // Данные по продажам (к примеру, январь 2025 года)
        List<SalesChartProjection> salesChartProjections = Arrays.asList(
                createSalesChartProjection("Cleaning 1", 2025L, 1L, 5L), // Январь 2025
                createSalesChartProjection("Cleaning 2", 2025L, 1L, 3L)  // Январь 2025
        );

        // Моки
        when(orderRepository.findTopCleanings(Mockito.any(), Mockito.any())).thenReturn(topCleaningsProjections);
        when(orderRepository.findSalesDataByMonth(Mockito.any())).thenReturn(salesChartProjections);

        // Получение результата
        SalesChartDataDTO result = orderService.getSalesChartData(topN, months);

        // Проверки
        assertNotNull(result);
        assertNotNull(result.getLabels());
        assertNotNull(result.getDatasets());

        // Проверка корректности работы с репозиториями
        verify(orderRepository, times(1)).findTopCleanings(Mockito.any(), Mockito.any());
        verify(orderRepository, times(1)).findSalesDataByMonth(Mockito.any());

        // Проверка, что datasets содержит правильные данные для каждого cleaning
        Map<String, List<Long>> datasets = result.getDatasets();

        // Печать для отладки
        System.out.println("Start YearMonth: " + result.getLabels());

        // Проверка для "Cleaning 1"
        List<Long> cleaning1Data = datasets.get("Cleaning 1");
        assertNotNull(cleaning1Data);
        assertEquals(6, cleaning1Data.size());

        // Печать для отладки
        System.out.println("Cleaning 1 Data: " + cleaning1Data);

        // Убедимся, что 5 стоит на пятом месяце (январь 2024)
        assertEquals(Long.valueOf(5), cleaning1Data.get(4)); // Январь 2024 должен быть равен 5
        for (int i = 0; i < 4; i++) {
            assertEquals(Long.valueOf(0), cleaning1Data.get(i));
        }
        for (int i = 5; i < cleaning1Data.size(); i++) {
            assertEquals(Long.valueOf(0), cleaning1Data.get(i));
        }


        // Проверка для "Cleaning 2"
        List<Long> cleaning2Data = datasets.get("Cleaning 2");
        assertNotNull(cleaning2Data);
        assertEquals(6, cleaning2Data.size());

        // Печать для отладки
        System.out.println("Cleaning 2 Data: " + cleaning2Data);

        // Убедимся, что 3 стоит на пятом месяце (январь 2024)
        assertEquals(Long.valueOf(3), cleaning2Data.get(4)); // Январь 2024 должен быть равен 3
        for (int i = 0; i < 4; i++) {
            assertEquals(Long.valueOf(0), cleaning2Data.get(i));
        }
        for (int i = 5; i < cleaning2Data.size(); i++) {
            assertEquals(Long.valueOf(0), cleaning2Data.get(i));
        }

        // Проверка для "Cleaning 3"
        List<Long> cleaning3Data = datasets.get("Cleaning 3");
        assertNotNull(cleaning3Data);
        assertEquals(6, cleaning3Data.size());

        // Печать для отладки
        System.out.println("Cleaning 3 Data: " + cleaning3Data);

        for (int i = 0; i < cleaning3Data.size(); i++) {
            assertEquals(Long.valueOf(0), cleaning3Data.get(i)); // "Cleaning 3" нет в salesData, поэтому все 0
        }
    }





    @Test
    void calculateTotalSales_ShouldReturnTotalSalesAmount() {
        BigDecimal expectedTotalSales = BigDecimal.valueOf(500.00);
        Order order = createOrder();
        order.setPrice(expectedTotalSales);
        when(orderRepository.findAll(Mockito.any(Specification.class))).thenReturn(Collections.singletonList(order));

        BigDecimal result = orderService.calculateTotalSales();

        assertEquals(expectedTotalSales, result);
        verify(orderRepository, times(1)).findAll(Mockito.any(Specification.class));
    }



    private WorkScheduleConfig createWorkScheduleConfig() {
        WorkScheduleConfig config = new WorkScheduleConfig();
        Map<String, WorkScheduleConfig.WorkDay> schedule = new HashMap<>();

        // Понедельник
        WorkScheduleConfig.WorkDay mondayWorkDay = new WorkScheduleConfig.WorkDay();
        mondayWorkDay.setStart("09:00");
        mondayWorkDay.setEnd("18:00");
        WorkScheduleConfig.WorkDay.Lunch mondayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        mondayLunch.setStart("13:00");
        mondayLunch.setEnd("14:00");
        mondayWorkDay.setLunch(mondayLunch);
        schedule.put("monday", mondayWorkDay);

        WorkScheduleConfig.WorkDay tuesdayWorkDay = new WorkScheduleConfig.WorkDay();
        tuesdayWorkDay.setStart("09:00");
        tuesdayWorkDay.setEnd("18:00");
        WorkScheduleConfig.WorkDay.Lunch tuesdayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        tuesdayLunch.setStart("13:00");
        tuesdayLunch.setEnd("14:00");
        tuesdayWorkDay.setLunch(tuesdayLunch);
        schedule.put("tuesday", tuesdayWorkDay);

        WorkScheduleConfig.WorkDay wednesdayWorkDay = new WorkScheduleConfig.WorkDay();
        wednesdayWorkDay.setStart("09:00");
        wednesdayWorkDay.setEnd("18:00");
        WorkScheduleConfig.WorkDay.Lunch wednesdayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        wednesdayLunch.setStart("13:00");
        wednesdayLunch.setEnd("14:00");
        wednesdayWorkDay.setLunch(wednesdayLunch);
        schedule.put("wednesday", wednesdayWorkDay);

        WorkScheduleConfig.WorkDay thursdayWorkDay = new WorkScheduleConfig.WorkDay();
        thursdayWorkDay.setStart("09:00");
        thursdayWorkDay.setEnd("18:00");
        WorkScheduleConfig.WorkDay.Lunch thursdayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        thursdayLunch.setStart("13:00");
        thursdayLunch.setEnd("14:00");
        thursdayWorkDay.setLunch(thursdayLunch);
        schedule.put("thursday", thursdayWorkDay);

        WorkScheduleConfig.WorkDay fridayWorkDay = new WorkScheduleConfig.WorkDay();
        fridayWorkDay.setStart("09:00");
        fridayWorkDay.setEnd("18:00");
        WorkScheduleConfig.WorkDay.Lunch fridayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        fridayLunch.setStart("13:00");
        fridayLunch.setEnd("14:00");
        fridayWorkDay.setLunch(fridayLunch);
        schedule.put("friday", fridayWorkDay);

        WorkScheduleConfig.WorkDay saturdayWorkDay = new WorkScheduleConfig.WorkDay();
        saturdayWorkDay.setStart("off");
        saturdayWorkDay.setEnd("off");
        WorkScheduleConfig.WorkDay.Lunch saturdayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        saturdayLunch.setStart("off");
        saturdayLunch.setEnd("off");
        saturdayWorkDay.setLunch(saturdayLunch);
        schedule.put("saturday", saturdayWorkDay);

        WorkScheduleConfig.WorkDay sundayWorkDay = new WorkScheduleConfig.WorkDay();
        sundayWorkDay.setStart("off");
        sundayWorkDay.setEnd("off");
        WorkScheduleConfig.WorkDay.Lunch sundayLunch = new WorkScheduleConfig.WorkDay.Lunch();
        sundayLunch.setStart("off");
        sundayLunch.setEnd("off");
        sundayWorkDay.setLunch(sundayLunch);
        schedule.put("sunday", sundayWorkDay);

        config.setSchedule(schedule);
        return config;
    }

    private OrderRequest createOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStartDate(LocalDate.now());
        orderRequest.setStartTime(LocalTime.now());
        orderRequest.setCustomerId(1L);
        orderRequest.setStatus(OrderStatus.NEW);
        OrderCleaningRequest orderCleaningRequest = new OrderCleaningRequest();
        orderCleaningRequest.setCleaningId(1L);
        orderCleaningRequest.setNumberUnits(2);
        orderRequest.setOrderCleanings(Collections.singletonList(orderCleaningRequest));
        return orderRequest;
    }

    private OrderResponse createOrderResponse() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setStartDate(LocalDate.now());
        orderResponse.setStartTime(LocalTime.now());
        orderResponse.setEndDate(LocalDate.now().plusDays(1));
        orderResponse.setTotalDuration(Duration.ofHours(2));
        orderResponse.setPrice(BigDecimal.valueOf(50.00));
        orderResponse.setStatus(OrderStatus.NEW);
        orderResponse.setCustomerId(1L);
        orderResponse.setCustomerName("Customer Name");
        OrderCleaningResponse orderCleaningResponse = new OrderCleaningResponse();
        orderCleaningResponse.setCleaningId(1L);
        orderCleaningResponse.setNumberUnits(2);
        orderCleaningResponse.setDurationCleaning(Duration.ofHours(2));
        orderCleaningResponse.setPrice(BigDecimal.valueOf(50.00));
        orderResponse.setOrderCleanings(Collections.singletonList(orderCleaningResponse));
        return orderResponse;
    }
    private OrderResponse createOrderResponse(Long id) {
        OrderResponse orderResponse = createOrderResponse();
        orderResponse.setId(id);
        return orderResponse;
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStartDate(LocalDate.now());
        order.setStartTime(LocalTime.now());
        order.setEndDate(LocalDate.now().plusDays(1));
        order.setTotalDuration(Duration.ofHours(2));
        order.setPrice(BigDecimal.valueOf(50.00));
        order.setStatus(OrderStatus.NEW);
        order.setCustomer(createCustomer(1L, "Customer 1"));
        order.setOrderCleanings(new ArrayList<>(Collections.singletonList(createOrderCleaning(1L, createCleaning(1L, "Cleaning 1"), 2))));
        return order;
    }
    private Order createOrder(Long id) {
        Order order = createOrder();
        order.setId(id);
        return order;
    }

    private Customer createCustomer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }
    private Cleaning createCleaning(Long id, String name) {
        Cleaning cleaning = new Cleaning();
        cleaning.setId(id);
        cleaning.setName(name);
        cleaning.setCleaningSpecifications(createCleaningSpecifications(1L, "Spec 1"));
        return cleaning;
    }
    private CleaningSpecifications createCleaningSpecifications(Long id, String name) {
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(id);
        cleaningSpecifications.setName(name);
        cleaningSpecifications.setTimeMultiplier(0.5);
        return cleaningSpecifications;
    }
    private OrderCleaning createOrderCleaning(Long id, Cleaning cleaning, Integer numberUnits) {
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setId(id);
        orderCleaning.setCleaning(cleaning);
        orderCleaning.setNumberUnits(numberUnits);
        orderCleaning.durationCleaning();
        orderCleaning.setPrice(BigDecimal.valueOf(25.00));
        return orderCleaning;
    }

    private TopCleaningProjection createTopCleaningProjection(String name, Long count) {
        return new TopCleaningProjection() {
            @Override
            public String getName() {
                return name;
            }
            @Override
            public Long getCount() {
                return count;
            }
        };
    }

    private SalesChartProjection createSalesChartProjection(String name, Long year, Long month, Long count) {
        return new SalesChartProjection() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Integer getYear() {
                return year.intValue();
            }

            @Override
            public Integer getMonth() {
                return month.intValue();
            }

            @Override
            public Long getCount() {
                return count;
            }
        };
    }
}
