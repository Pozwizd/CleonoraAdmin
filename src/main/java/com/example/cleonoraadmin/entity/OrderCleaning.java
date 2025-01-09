package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 OrderCleaning
- id: Long
- durationCleaning: Duration
- numberUnits: Integer
- price: BigDecimal
- cleaning: Cleaning
- timeSlots: List<TimeSlot>
 --
 - getId(): Long
 - setId(Long id)
 - getDurationCleaning(): Duration
 - setDurationCleaning(Duration durationCleaning)
 - getNumberUnits(): Integer
 - setNumberUnits(Integer numberUnits)
 - getPrice(): BigDecimal
 - setPrice(BigDecimal price)
 - getCleaning(): Cleaning
 - setCleaning(Cleaning cleaning)
 - getTimeSlots(): List<TimeSlot>
 - setTimeSlots(List<TimeSlot> timeSlots)
 */

@Entity
@Data
public class OrderCleaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Duration durationCleaning;
    private Integer numberUnits;
    @Column(nullable = false)
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "cleaning_id", nullable = false)
    private Cleaning cleaning;
    @OneToMany(mappedBy = "orderCleaning", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setOrderCleaning(this);
    }
    public void removeTimeSlot(TimeSlot timeSlot) {
        timeSlots.remove(timeSlot);
        timeSlot.setOrderCleaning(null);
    }
    public void durationCleaning() {
        double totalDurationMinutes = cleaning.getCleaningSpecifications().getTimeMultiplier() * numberUnits * 60;
        this.durationCleaning = Duration.ofMinutes((long) totalDurationMinutes);
    }
}

