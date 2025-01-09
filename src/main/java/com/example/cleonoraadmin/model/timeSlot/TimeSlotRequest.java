package com.example.cleonoraadmin.model.timeSlot;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


// Так как TimeSlot создается не напрямую пользователем, а системой, Request DTO может не понадобиться.
// Но если нужен, например, для административных функций, то может выглядеть так:
@Data
public class TimeSlotRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long orderCleaningId;
}