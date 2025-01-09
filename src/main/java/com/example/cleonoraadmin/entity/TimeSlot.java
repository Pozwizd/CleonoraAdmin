package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
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
    private boolean isAvailable = true; // Флаг доступности
    @ManyToOne
    @JoinColumn(name = "order_cleaning_id")
    private OrderCleaning orderCleaning;
    @ManyToOne
    @JoinColumn(name = "workday_id", nullable = false)
    private Workday workday;

    // Метод для расчета продолжительности TimeSlot
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}
