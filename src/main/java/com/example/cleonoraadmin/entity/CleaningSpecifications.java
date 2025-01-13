package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 CleaningSpecifications
- id: Long
- name: String
- baseCost: Double
- complexityCoefficient: Double
- ecoFriendlyExtraCost: Double
- frequencyCoefficient: Double
- locationCoefficient: Double
- timeMultiplier: Double
- unit: String
- icon: String
--
+ getId(): Long
+ setId(Long id)
+ getName(): String
+ setName(String name)
+ getBaseCost(): Double
+ setBaseCost(Double baseCost)
+ getComplexityCoefficient(): Double
+ setComplexityCoefficient(Double complexityCoefficient)
+ getEcoFriendlyExtraCost(): Double
+ setEcoFriendlyExtraCost(Double ecoFriendlyExtraCost)
+ getFrequencyCoefficient(): Double
+ setFrequencyCoefficient(Double frequencyCoefficient)
+ getLocationCoefficient(): Double
+ setLocationCoefficient(Double locationCoefficient)
+ getTimeMultiplier(): Double
+ setTimeMultiplier(Double timeMultiplier)
+ getUnit(): String
+ setUnit(String unit)
+ getIcon(): String
+ setIcon(String icon)
 */

@Entity
@Data
public class CleaningSpecifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double baseCost;
    @Column(nullable = false)
    private Double complexityCoefficient;
    private Double ecoFriendlyExtraCost;
    private Double frequencyCoefficient;
    private Double locationCoefficient;
    private Double timeMultiplier;
    private String unit;
    private String icon;
}