package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.repository.OrderCleaningRepository;
import com.example.cleanorarest.repository.OrderRepository;
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

/**
 * Unit tests for {@link OrderCleaningServiceImp}.
 */
@ExtendWith(MockitoExtension.class)
class OrderCleaningServiceImpTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCleaningRepository orderCleaningRepository;

    @InjectMocks
    private OrderCleaningServiceImp orderCleaningService;

    /**
     * Test for {@link OrderCleaningServiceImp#ifOrdersMoreThan(int)}.
     * Should return true if the order count is greater than the given number.
     */
    @Test
    void ifOrdersMoreThan_OrderCountGreaterThanValue_ReturnsTrue() {
        // Arrange
        int value = 5;
        when(orderRepository.count()).thenReturn(6L);

        // Act
        boolean result = orderCleaningService.ifOrdersMoreThan(value);

        // Assert
        assertTrue(result);
        verify(orderRepository).count();
    }
   /**
    * Test for {@link OrderCleaningServiceImp#ifOrdersMoreThan(int)}.
    * Should return false if the order count is not greater than the given number.
    */

   @Test
   void ifOrdersMoreThan_OrderCountNotGreaterThanValue_ReturnsFalse() {
       // Arrange
       int value = 5;
       when(orderRepository.count()).thenReturn(5L);

       // Act
       boolean result = orderCleaningService.ifOrdersMoreThan(value);

       // Assert
       assertFalse(result);
       verify(orderRepository).count();
   }
   /**
    * Test for {@link OrderCleaningServiceImp#deleteOrderCleanings(Order)}.
    * Verifies that the deleteAll method is called with the correct list of OrderCleanings.
    */
   @Test
   void deleteOrderCleanings_CallsRepositoryDeleteAll() {
       // Arrange
       Order order = new Order();
       order.setOrderCleanings(new ArrayList<>());

       // Act
       orderCleaningService.deleteOrderCleanings(order);

       // Assert
        verify(orderCleaningRepository).deleteAll(order.getOrderCleanings());
    }
   }
