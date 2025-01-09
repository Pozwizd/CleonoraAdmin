package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.mapper.CustomerMapper;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.model.customer.CustomerResponse;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.service.CustomerService;
import com.example.cleonoraadmin.specification.CustomerSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Page<CustomerResponse> getPageAllCustomers(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return customerMapper.toResponsePage(customerRepository
                .findAll(
                        CustomerSpecification.search(search)
                                .and(
                                        CustomerSpecification.byDeleted(false)), pageRequest));
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public CustomerResponse saveNewCustomer(CustomerRequest customerRequest) {
        try {
            Customer customer = customerMapper.toEntity(customerRequest);
            Customer savedCustomer = customerRepository.save(customer);
            return customerMapper.toResponse(savedCustomer);
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового клиента: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении нового клиента", e);
        }
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isEmpty()) {
            throw new EntityNotFoundException("Клиент с ID " + id + " не найден");
        }
        Customer customerToUpdate = existingCustomer.get();
        customerMapper.partialUpdate(customerRequest, customerToUpdate);
        Customer updatedCustomer = customerRepository.save(customerToUpdate);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public boolean deleteCustomerById(Long id) {
        if (customerRepository.existsById(id)) {
            try {
                customerRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                log.error("Ошибка при удалении клиента с ID {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Ошибка при удалении клиента", e);
            }
        } else {
            throw new EntityNotFoundException("Клиент с ID " + id + " не найден");
        }
    }

    @Override
    public boolean ifCustomerCountMoreThan(int i) {
        return customerRepository.count() > i;
    }

    @Override
    public CustomerResponse getCustomerResponseById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new EntityNotFoundException("Клиент с ID " + id + " не найден");
        }
        return customerMapper.toResponse(customer.get());
    }


    @Override
    public Integer countAllCustomers() {
        return customerRepository.countAllCustomers();
    }

    /**
     * @return
     */
    @Override
    public Integer countDailyNewCustomers() {

        return customerRepository.findAll(CustomerSpecification.registrationLastDaily()).size();
    }

//    @Override
//    public Integer countTodayNewCustomers() {
//        return customerRepository.countTodayNewCustomers();
//    }
//
//    @Override
//    public Long getCountOfCustomersWithOrdersLastWeek() {
//        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
//        return customerRepository.countUniqueCustomersWithOrdersSince(oneWeekAgo);
//    }
}
