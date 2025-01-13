package com.example.cleonoraadmin.entity;


import jakarta.persistence.*;
import lombok.Data;
import net.datafaker.Faker;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 Customer
- id: Long
- name: String
- surname: String
- email: String
- phoneNumber: String
- password: String
- isActive: Boolean
- orders: List<Order>
- longitude: String
- latitude: String
- street: String
- numberStreet: String
- tower: String
- registrationDate: LocalDateTime
- deleted: Boolean
--
+ getId(): Long
+ setId(Long id): void
+ getName(): String
+ setName(String name): void
+ getSurname(): String
+ setSurname(String surname): void
+ getEmail(): String
+ setEmail(String email): void
+ getPhoneNumber(): String
+ setPhoneNumber(String phoneNumber): void
+ getPassword(): String
+ setPassword(String password): void
+ getIsActive(): Boolean
+ setIsActive(Boolean isActive): void
+ getOrders(): List<Order>
+ setOrders(List<Order> orders): void
+ getLongitude(): String
+ setLongitude(String longitude): void
+ getLatitude(): String
+ setLatitude(String latitude): void
+ getStreet(): String
+ setStreet(String street): void
+ getNumberStreet(): String
+ setNumberStreet(String numberStreet): void
+ getTower(): String
+ setTower(String tower): void
+ getRegistrationDate(): LocalDateTime
+ setRegistrationDate(LocalDateTime registrationDate): void
+ getDeleted(): Boolean
+ setDeleted(Boolean deleted): void
 */
@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password = new Random().nextInt(100000, 999999) + "";
    private Boolean isActive = true;
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    private LocalDateTime registrationDate = LocalDateTime.now();
    private Boolean deleted = false;
}
