package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

/**
 Orders
 - id: Long
 - date: LocalDate
 - duration: Duration
 - price: BigDecimal
 - status: OrderStatus
 - user: AppUser
 - orderServices: List<OrderService>
 - workdays: List<Workday>
 --
 - getId(): Long
 - setId(Long id)
 - getDate(): LocalDate
 - setDate(LocalDate date)
 - getDuration(): Duration
 - setDuration(Duration duration)
 - getPrice(): BigDecimal
 - setPrice(BigDecimal price)
 - getStatus(): OrderStatus
 - setStatus(OrderStatus status)
 - getUser(): AppUser
 - setUser(AppUser user)
 - getOrderServices(): List<OrderService>
 - setOrderServices(List<OrderService> orderServices)
 - getWorkdays(): List<Workday>
 - setWorkdays(List<Workday> workdays)

 */

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;


}

