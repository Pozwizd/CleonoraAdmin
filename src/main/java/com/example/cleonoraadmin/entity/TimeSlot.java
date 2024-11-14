package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 TimeSlot
 - id: Long
 - date: LocalDate
 - startTime: LocalTime
 - endTime: LocalTime
 - orderService: OrderService
 - workday: Workday
 --
 - getId(): Long
 - setId(Long id)
 - getDate(): LocalDate
 - setDate(LocalDate date)
 - getStartTime(): LocalTime
 - setStartTime(LocalTime startTime)
 - getEndTime(): LocalTime
 - setEndTime(LocalTime endTime)
 - getOrderService(): OrderService
 - setOrderService(OrderService orderService)
 - getWorkday(): Workday
 - setWorkday(Workday workday)
 */

@Entity
@Data
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "order_service_id", nullable = false)
    private OrderService orderService;

    @ManyToOne
    @JoinColumn(name = "workday_id", nullable = false)
    private Workday workday;

}
