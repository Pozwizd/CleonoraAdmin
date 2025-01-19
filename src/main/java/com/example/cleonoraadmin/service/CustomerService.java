package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.model.customer.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CustomerService {

    Customer save(Customer customer);

    Page<CustomerResponse> getPageAllCustomers(int page, Integer size, String search);

    Optional<Customer> getCustomerById(Long id);

    CustomerResponse saveNewCustomer(@Valid CustomerRequest customerRequest);

    CustomerResponse updateCustomer(Long id, @Valid CustomerRequest customerRequest);

    boolean deleteCustomerById(Long id);

    boolean ifCustomerCountMoreThan(int i);

    CustomerResponse getCustomerResponseById(Long id);

    Integer countAllCustomers();

    Integer countDailyNewCustomers();

    Integer countActiveCustomers(int days);

    Integer countActiveCustomersWeekAgo(int days);

    Customer getCustomerByEmail(String email);
}
