package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;



public interface OrderCleaningService {

    boolean ifOrdersMoreThan(int i);
}