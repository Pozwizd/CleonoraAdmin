package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 MeasurementUnit
 - id: Long
 - unit: String
 - icon: String
 --
 - getId(): Long
 - setId(Long id)
 - getUnit(): String
 - setUnit(String unit)
 - getIcon(): String
 - setIcon(String icon)
 */


@Entity
@Data
public class MeasurementUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String unit;

    private String icon;

    // Геттеры и сеттеры
}
