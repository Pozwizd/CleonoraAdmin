package com.example.cleonoraadmin.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

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
- deleted: Boolean
 --
 - getId(): Long
 - setId(Long id)
 - getName(): String
 - setName(String name)
 - getSurname(): String
 - setSurname(String surname)
 - getEmail(): String
 - setEmail(String email)
 - getPhoneNumber(): String
 - setPhoneNumber(String phoneNumber)
 - getPassword(): String
 - setPassword(String password)
 - getIsActive(): Boolean
 - setIsActive(Boolean isActive)
 - getOrders(): List<Order>
 - setOrders(List<Order> orders)
 - getDeleted(): Boolean
 - setDeleted(Boolean deleted)
 */
@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean isActive = true;
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;


    private String longitude;
    private String latitude;

    private String street;
    private String numberStreet;
    private String tower;

    private LocalDateTime registrationDate = LocalDateTime.now();
    private Boolean deleted = false;
}
