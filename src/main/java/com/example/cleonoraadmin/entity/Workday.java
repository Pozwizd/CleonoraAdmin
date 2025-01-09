package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 Workday
 - id: Long
 - date: LocalDate
 - startTime: LocalTime
 - endTime: LocalTime
 - totalAvailableTime: Duration
 --
 - getId(): Long
 - setId(Long id)
 - getDate(): LocalDate
 - setDate(LocalDate date)
 - getStartTime(): LocalTime
 - setStartTime(LocalTime startTime)
 - getEndTime(): LocalTime
 - setEndTime(LocalTime endTime)
 - getTotalAvailableTime(): Duration
 - setTotalAvailableTime(Duration totalAvailableTime)
 */



@Entity
@Data
public class Workday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime lunchStart; // Начало обеда
    private LocalTime lunchEnd;   // Конец обеда

    @OneToMany(mappedBy = "workday", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();

    public Duration getTotalAvailableTime() {
        return Duration.between(startTime, endTime).minus(
                Duration.between(lunchStart, lunchEnd)); // Исключаем обеденное время
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setWorkday(this);
    }
}