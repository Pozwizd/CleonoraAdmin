package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 CleaningService
 - id: Long
 - name: String
 - baseCost: Double
 - complexityCoefficient: Double
 - extraCostEco: Double
 - frequencyCoefficient: Double
 - locationCoefficient: Double
 - timeMultiplier: Double
 - measurementUnit: MeasurementUnit
 --
 - getId(): Long
 - setId(Long id)
 - getName(): String
 - setName(String name)
 - getBaseCost(): Double
 - setBaseCost(Double baseCost)
 - getComplexityCoefficient(): Double
 - setComplexityCoefficient(Double complexityCoefficient)
 - getExtraCostEco(): Double
 - setExtraCostEco(Double extraCostEco)
 - getFrequencyCoefficient(): Double
 - setFrequencyCoefficient(Double frequencyCoefficient)
 - getLocationCoefficient(): Double
 - setLocationCoefficient(Double locationCoefficient)
 - getTimeMultiplier(): Double
 - setTimeMultiplier(Double timeMultiplier)
 - getMeasurementUnit(): MeasurementUnit
 - setMeasurementUnit(MeasurementUnit measurementUnit)
 */

@Entity
@Data
public class CleaningService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double baseCost;

    @Column(nullable = false)
    private Double complexityCoefficient;

    private Double extraCostEco;

    private Double frequencyCoefficient;

    private Double locationCoefficient;

    @Column(nullable = false)
    private Double timeMultiplier;

    @ManyToOne
    @JoinColumn(name = "measurement_unit_id", nullable = false)
    private MeasurementUnit measurementUnit;

}