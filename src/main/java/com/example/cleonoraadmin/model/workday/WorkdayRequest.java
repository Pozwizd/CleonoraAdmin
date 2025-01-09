package com.example.cleonoraadmin.model.workday;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkdayRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}