package com.example.cleonoraadmin.model.timeSlot;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class TimeSlotResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
}