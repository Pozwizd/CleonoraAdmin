package com.example.cleonoraadmin.model.order;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import com.example.cleonoraadmin.validators.order.CheckTime;
import com.example.cleonoraadmin.validators.order.ValidWorkday;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request model for {@link Order}
 */
@Data
@CheckTime
@ValidWorkday

public class OrderRequest {
    private Long id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Long customerId;
    private OrderStatus status;

    private List<OrderCleaningRequest> orderCleanings;

    @Valid
    private CustomerAddressRequest address;
}