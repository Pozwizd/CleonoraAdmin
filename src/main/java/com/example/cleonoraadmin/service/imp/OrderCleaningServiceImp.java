package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.repository.OrderCleaningRepository;
import com.example.cleonoraadmin.repository.OrderRepository;
import com.example.cleonoraadmin.service.OrderCleaningService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class OrderCleaningServiceImp implements OrderCleaningService {

    private final OrderRepository orderRepository;
    private final OrderCleaningRepository orderCleaningRepository;


    @Override
    public boolean ifOrdersMoreThan(int i) {
        return orderRepository.count() > i;
    }

    @Transactional
    public void deleteOrderCleanings(Order existingOrder) {
        orderCleaningRepository.deleteAll(existingOrder.getOrderCleanings());
    }
}
