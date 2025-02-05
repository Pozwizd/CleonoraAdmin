package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.entity.Order;

import java.time.LocalDate;

public interface OrderCleaningSchedulingService {

    void createTimeSlotsForOrder(Order order);


    LocalDate calculateEndDate(Order order);
}
