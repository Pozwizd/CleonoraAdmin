package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

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

    private Duration totalAvailableTime;
}
