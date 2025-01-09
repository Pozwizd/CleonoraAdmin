package com.example.cleonoraadmin.model.order;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import com.example.cleonoraadmin.model.customer.CustomerResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * Response model for {@link Order}
 */

@Data
public class OrderResponse {
    private Long id;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private Duration totalDuration;
    private BigDecimal price;
    private OrderStatus status;
    private Long customerId;
    private String customerName;
    private List<OrderCleaningResponse> orderCleanings;

}