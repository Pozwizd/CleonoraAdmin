package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.model.SalesChartDataDTO;
import com.example.cleonoraadmin.model.TopCleaning;
import com.example.cleonoraadmin.model.TopCleaningDTO;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {


    @Transactional
    OrderResponse createOrder(OrderRequest orderRequest);

    @Transactional
    OrderResponse updateOrder(OrderRequest updatedOrderRequest);

    Page<OrderResponse> getPageAllOrders(int page, Integer size, String search);

    Page<OrderResponse> getPageAllOrdersLastWeek(int page, Integer size, String search);

    OrderResponse getOrderById(Long id);

    boolean deleteOrder(Long id);

    BigDecimal calculateSalesLastWeek();

    boolean ifOrderMoreThan(int i);

    Integer countCompletedOrders();

    Integer countDailyCompletedOrders();


    List<TopCleaningDTO> getTopCleanings(int topN, int months);

    SalesChartDataDTO getSalesChartData(int topN, int months);

    BigDecimal calculateTotalSales();
}
