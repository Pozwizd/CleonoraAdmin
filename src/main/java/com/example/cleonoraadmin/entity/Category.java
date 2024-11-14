package com.example.cleonoraadmin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

/**
 Category
 - id: Long
 - name: String
 - description: String
 --
 - getId(): Long
 - setId(Long id)
 - getName(): String
 - setName(String name)
 - getDescription(): String
 - setDescription(String description)
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