package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

/**
 OrderService
 -id: Long
 -durationService: Duration
 -numberUnits: Integer
 -price: BigDecimal
 -order: Order
 -service: Service
 --
 -getId(): Long
 -setId(Long id)
 -getDurationService(): Duration
 -setDurationService(Duration durationService)
 -getNumberUnits(): Integer
 -setNumberUnits(Integer numberUnits)
 -getPrice(): BigDecimal
 -setPrice(BigDecimal price)
 -getOrder(): Order
 -setOrder(Order order)
 -getService(): Service
 -setService(Service service)

 */

@Entity
@Data
public class OrderService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Duration durationService;

    private Integer numberUnits;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

}

