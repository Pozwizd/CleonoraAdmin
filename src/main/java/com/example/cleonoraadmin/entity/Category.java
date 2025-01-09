package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 Category
- id: Long
- name: String
- description: String
--
+ getId(): Long
+ setId(Long id)
+ getName(): String
+ setName(String name)
+ getDescription(): String
+ setDescription(String description)
 */
@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

}