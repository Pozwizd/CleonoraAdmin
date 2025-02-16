package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.mapper.CustomerMapper;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.model.customer.CustomerResponse;
import com.example.cleonoraadmin.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImp customerService;

    @Test
    void save_ShouldReturnSavedCustomer() {
        Customer customerToSave = createCustomer(1L, "Клиент 1");
        Customer savedCustomer = createCustomer(1L, "Клиент 1");
        when(customerRepository.save(customerToSave)).thenReturn(savedCustomer);

        Customer result = customerService.save(customerToSave);

        assertNotNull(result);
        assertEquals(savedCustomer, result);
        verify(customerRepository, times(1)).save(customerToSave);
    }

    @Test
    void getPageAllCustomers_WithSearch_ShouldReturnPageOfCustomerResponses() {
        int page = 0;
        int size = 10;
        String search = "Клиент";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Customer> customerPage = new PageImpl<>(List.of(createCustomer(1L, "Клиент 1"), createCustomer(2L, "Клиент 2")), pageRequest, 2);
        Page<CustomerResponse> customerResponsePage = new PageImpl<>(List.of(createCustomerResponse(1L, "Клиент 1"), createCustomerResponse(2L, "Клиент 2")), pageRequest, 2);

        when(customerRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(customerPage);
        when(customerMapper.toResponsePage(customerPage)).thenReturn(customerResponsePage);

        Page<CustomerResponse> resultPage = customerService.getPageAllCustomers(page, size, search);

        assertNotNull(resultPage);
        assertEquals(customerResponsePage, resultPage);
        verify(customerRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(customerMapper, times(1)).toResponsePage(customerPage);
    }

    @Test
    void getPageAllCustomers_WithoutSearch_ShouldReturnPageOfCustomerResponses() {
        int page = 0;
        int size = 10;
        String search = null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Customer> customerPage = new PageImpl<>(List.of(createCustomer(1L, "Клиент 1"), createCustomer(2L, "Клиент 2")), pageRequest, 2);
        Page<CustomerResponse> customerResponsePage = new PageImpl<>(List.of(createCustomerResponse(1L, "Клиент 1"), createCustomerResponse(2L, "Клиент 2")), pageRequest, 2);

        when(customerRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(customerPage);
        when(customerMapper.toResponsePage(customerPage)).thenReturn(customerResponsePage);

        Page<CustomerResponse> resultPage = customerService.getPageAllCustomers(page, size, search);

        assertNotNull(resultPage);
        assertEquals(customerResponsePage, resultPage);
        verify(customerRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(customerMapper, times(1)).toResponsePage(customerPage);
    }

    @Test
    void getCustomerById_CustomerExists_ShouldReturnOptionalOfCustomer() {
        Long customerId = 1L;
        Customer customer = createCustomer(customerId, "Клиент 1");
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void getCustomerById_CustomerNotFound_ShouldReturnEmptyOptional() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById(customerId);

        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void saveNewCustomer_SuccessfulSave_ShouldReturnCustomerResponse() {
        CustomerRequest customerRequest = createCustomerRequest("Клиент 1");
        Customer customerToSave = createCustomer(null, "Клиент 1");
        Customer savedCustomer = createCustomer(1L, "Клиент 1");
        CustomerResponse customerResponse = createCustomerResponse(1L, "Клиент 1");

        when(customerMapper.toEntity(customerRequest)).thenReturn(customerToSave);
        when(customerRepository.save(customerToSave)).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(customerResponse);

        CustomerResponse result = customerService.saveNewCustomer(customerRequest);

        assertNotNull(result);
        assertEquals(customerResponse, result);
        verify(customerMapper, times(1)).toEntity(customerRequest);
        verify(customerRepository, times(1)).save(customerToSave);
        verify(customerMapper, times(1)).toResponse(savedCustomer);
    }

    @Test
    void saveNewCustomer_ExceptionDuringSave_ShouldThrowRuntimeException() {
        CustomerRequest customerRequest = createCustomerRequest("Клиент 1");
        Customer customerToSave = createCustomer(null, "Клиент 1");

        when(customerMapper.toEntity(customerRequest)).thenReturn(customerToSave);
        when(customerRepository.save(customerToSave)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> customerService.saveNewCustomer(customerRequest));

        verify(customerMapper, times(1)).toEntity(customerRequest);
        verify(customerRepository, times(1)).save(customerToSave);
        verifyNoMoreInteractions(customerMapper);
    }

    @Test
    void updateCustomer_CustomerExists_ShouldReturnUpdatedCustomerResponse() {
        Long customerId = 1L;
        CustomerRequest customerRequest = createCustomerRequest("Клиент Обновленный");
        Customer existingCustomer = createCustomer(customerId, "Клиент 1");
        Customer updatedCustomerEntity = createCustomer(customerId, "Клиент Обновленный");
        CustomerResponse updatedCustomerResponse = createCustomerResponse(customerId, "Клиент Обновленный");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerMapper.partialUpdate(customerRequest, existingCustomer)).thenReturn(updatedCustomerEntity);
        when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomerEntity);
        when(customerMapper.toResponse(updatedCustomerEntity)).thenReturn(updatedCustomerResponse);

        CustomerResponse result = customerService.updateCustomer(customerId, customerRequest);

        assertNotNull(result);
        assertEquals(updatedCustomerResponse, result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerMapper, times(1)).partialUpdate(customerRequest, existingCustomer);
        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerMapper, times(1)).toResponse(updatedCustomerEntity);
    }

    @Test
    void updateCustomer_CustomerNotFound_ShouldThrowEntityNotFoundException() {
        Long customerId = 1L;
        CustomerRequest customerRequest = createCustomerRequest("Клиент Обновленный");
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.updateCustomer(customerId, customerRequest));

        verify(customerRepository, times(1)).findById(customerId);
        verifyNoMoreInteractions(customerMapper);
    }

    @Test
    void deleteCustomerById_CustomerExists_SuccessfulDelete_ShouldReturnTrue() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(customerId);

        boolean result = customerService.deleteCustomerById(customerId);

        assertTrue(result);
        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void deleteCustomerById_CustomerExists_ExceptionDuringDelete_ShouldThrowRuntimeException() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(customerRepository).deleteById(customerId);

        assertThrows(RuntimeException.class, () -> customerService.deleteCustomerById(customerId));

        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void deleteCustomerById_CustomerNotFound_ShouldThrowEntityNotFoundException() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteCustomerById(customerId));

        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, never()).deleteById(customerId);
    }

    @Test
    void ifCustomerCountMoreThan_CountMoreThanI_ShouldReturnTrue() {
        int i = 5;
        when(customerRepository.count()).thenReturn(10L);

        boolean result = customerService.ifCustomerCountMoreThan(i);

        assertTrue(result);
        verify(customerRepository, times(1)).count();
    }

    @Test
    void ifCustomerCountMoreThan_CountLessThanOrEqualI_ShouldReturnFalse() {
        int i = 10;
        when(customerRepository.count()).thenReturn(5L);

        boolean result = customerService.ifCustomerCountMoreThan(i);

        assertFalse(result);
        verify(customerRepository, times(1)).count();
    }

    @Test
    void getCustomerResponseById_CustomerExists_ShouldReturnCustomerResponse() {
        Long customerId = 1L;
        Customer customer = createCustomer(customerId, "Клиент 1");
        CustomerResponse customerResponse = createCustomerResponse(customerId, "Клиент 1");
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = customerService.getCustomerResponseById(customerId);

        assertNotNull(result);
        assertEquals(customerResponse, result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerMapper, times(1)).toResponse(customer);
    }

    @Test
    void getCustomerResponseById_CustomerNotFound_ShouldThrowEntityNotFoundException() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomerResponseById(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verifyNoMoreInteractions(customerMapper);
    }

    @Test
    void countAllCustomers_ShouldReturnCustomerCount() {
        Integer expectedCount = 15;
        when(customerRepository.countAllCustomers()).thenReturn(expectedCount);

        Integer result = customerService.countAllCustomers();

        assertEquals(expectedCount, result);
        verify(customerRepository, times(1)).countAllCustomers();
    }

    @Test
    void countDailyNewCustomers_ShouldReturnDailyNewCustomersCount() {
        int expectedCount = 3;
        List<Customer> dailyNewCustomers = List.of(createCustomer(1L, "Клиент А"), createCustomer(2L, "Клиент Б"), createCustomer(3L, "Клиент В"));
        when(customerRepository.findAll(Mockito.any(Specification.class))).thenReturn(dailyNewCustomers);

        Integer result = customerService.countDailyNewCustomers();

        assertEquals(expectedCount, result);
        verify(customerRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void countActiveCustomers_ShouldReturnActiveCustomersCount() {
        int days = 7;
        int expectedCount = 7;
        List<Customer> activeCustomers = List.of(
                createCustomer(1L, "Клиент А"), createCustomer(2L, "Клиент Б"), createCustomer(3L, "Клиент В"),
                createCustomer(4L, "Клиент Г"), createCustomer(5L, "Клиент Д"), createCustomer(6L, "Клиент Е"),
                createCustomer(7L, "Клиент Ж")
        );
        when(customerRepository.findAll(Mockito.any(Specification.class))).thenReturn(activeCustomers);

        Integer result = customerService.countActiveCustomers(days);

        assertEquals(expectedCount, result);
        verify(customerRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void countActiveCustomersWeekAgo_ShouldReturnActiveCustomersWeekAgoCount() {
        int days = 7;
        int expectedCount = 5;
        List<Customer> activeCustomersWeekAgo = List.of(
                createCustomer(1L, "Клиент А"), createCustomer(2L, "Клиент Б"), createCustomer(3L, "Клиент В"),
                createCustomer(4L, "Клиент Г"), createCustomer(5L, "Клиент Д")
        );
        when(customerRepository.findAll(Mockito.any(Specification.class))).thenReturn(activeCustomersWeekAgo);

        Integer result = customerService.countActiveCustomersWeekAgo(days);

        assertEquals(expectedCount, result);
        verify(customerRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void getCustomerByEmail_CustomerExists_ShouldReturnCustomer() {
        String email = "client1@example.com";
        Customer customer = createCustomer(1L, "Клиент 1");
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerByEmail(email);

        assertNotNull(result);
        assertEquals(customer, result);
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    void getCustomerByEmail_CustomerNotFound_ShouldReturnNull() {
        String email = "nonexistent@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        Customer result = customerService.getCustomerByEmail(email);

        assertNull(result);
        verify(customerRepository, times(1)).findByEmail(email);
    }


    // Helper methods for creating test data
    private Customer createCustomer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(name.replace(" ", "") + "@example.com");
        customer.setPhoneNumber("+380501234567");
        customer.setIsActive(true);
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setDeleted(false);
        return customer;
    }

    private CustomerRequest createCustomerRequest(String name) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName(name);
        customerRequest.setEmail(name.replace(" ", "") + "@example.com");
        customerRequest.setPhoneNumber("+380501234567");
        customerRequest.setIsActive(true);
        return customerRequest;
    }

    private CustomerResponse createCustomerResponse(Long id, String name) {
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(id);
        customerResponse.setName(name);
        customerResponse.setEmail(name.replace(" ", "") + "@example.com");
        customerResponse.setPhoneNumber("+380501234567");
        customerResponse.setIsActive(true);
        return customerResponse;
    }
}
