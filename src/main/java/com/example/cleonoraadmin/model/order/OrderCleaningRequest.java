package com.example.cleonoraadmin.model.order;

import com.example.cleonoraadmin.entity.OrderCleaning;
import lombok.Data;

/**
 * Request model for {@link OrderCleaning}
 */

/**
 OrderCleaningRequest
- id: Long
- numberUnits: Integer
- cleaningId: Long
 */
@Data
public class OrderCleaningRequest {
    private Long id;
    private Integer numberUnits;
    private Long cleaningId;
}