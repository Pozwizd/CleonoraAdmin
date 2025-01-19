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
- lunchStart: LocalTime
- lunchEnd: LocalTime
- timeSlots: List<TimeSlot>
--
+ getTotalAvailableTime(): Duration
+ addTimeSlot(TimeSlot timeSlot): void
+ getId(): Long
+ setId(Long id): void
+ getDate(): LocalDate
+ setDate(LocalDate date): void
+ getStartTime(): LocalTime
+ setStartTime(LocalTime startTime): void
+ getEndTime(): LocalTime
+ setEndTime(LocalTime endTime): void
+ getLunchStart(): LocalTime
+ setLunchStart(LocalTime lunchStart): void
+ getLunchEnd(): LocalTime
+ setLunchEnd(LocalTime lunchEnd): void
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
    private LocalTime lunchStart;
    private LocalTime lunchEnd;

    @OneToMany(mappedBy = "workday", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();

    public Duration getTotalAvailableTime() {
        return Duration.between(startTime, endTime).minus(
                Duration.between(lunchStart, lunchEnd));
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setWorkday(this);
    }
}