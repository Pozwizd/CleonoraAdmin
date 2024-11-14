package com.example.cleonoraadmin.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


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

    @Enumerated(EnumType.STRING)
    private AdminRole role = AdminRole.ADMIN;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;


}
