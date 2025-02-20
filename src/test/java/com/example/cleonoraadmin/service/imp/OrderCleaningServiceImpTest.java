package com.example.cleonoraadmin.service.imp;


import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.repository.OrderCleaningRepository;
import com.example.cleonoraadmin.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderCleaningServiceImpTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCleaningRepository orderCleaningRepository;

    @InjectMocks
    private OrderCleaningServiceImp orderCleaningService;


    @Test
    void ifOrdersMoreThan_OrderCountGreaterThanValue_ReturnsTrue() {
        int value = 5;
        when(orderRepository.count()).thenReturn(6L);

        boolean result = orderCleaningService.ifOrdersMoreThan(value);

        assertTrue(result);
        verify(orderRepository).count();
    }

   @Test
   void ifOrdersMoreThan_OrderCountNotGreaterThanValue_ReturnsFalse() {
       int value = 5;
       when(orderRepository.count()).thenReturn(5L);

       boolean result = orderCleaningService.ifOrdersMoreThan(value);

       assertFalse(result);
       verify(orderRepository).count();
   }

   @Test
   void deleteOrderCleanings_CallsRepositoryDeleteAll() {
       Order order = new Order();
       order.setOrderCleanings(new ArrayList<>());

       orderCleaningService.deleteOrderCleanings(order);

        verify(orderCleaningRepository).deleteAll(order.getOrderCleanings());
    }
   }
