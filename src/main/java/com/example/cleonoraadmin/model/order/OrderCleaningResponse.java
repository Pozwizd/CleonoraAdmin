package com.example.cleonoraadmin.model.order;

import com.example.cleonoraadmin.entity.OrderCleaning;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;


/**
 * Response model for {@link OrderCleaning}
 */
@Data
public class OrderCleaningResponse {
    private Long id;
    private Integer numberUnits;
    private Long cleaningId;
    private Duration durationCleaning;
    private BigDecimal price;
}