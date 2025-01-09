package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.mapper.OrderMapper;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.repository.OrderRepository;
import com.example.cleonoraadmin.repository.CleaningRepository;
import com.example.cleonoraadmin.service.OrderCleaningService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import com.example.cleonoraadmin.specification.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class OrderCleaningServiceImp implements OrderCleaningService {

    private final OrderRepository orderRepository;
    private final CleaningRepository cleaningRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;




    @Override
    public boolean ifOrdersMoreThan(int i) {
        return orderRepository.count() > i;
    }
}