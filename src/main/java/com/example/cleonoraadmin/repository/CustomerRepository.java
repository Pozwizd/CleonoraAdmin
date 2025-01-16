package com.example.cleonoraadmin.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.cleonoraadmin.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);


    @Query("SELECT COUNT(c) FROM Customer c")
    Integer countAllCustomers();

    Optional<Customer> findByPhoneNumber(String s);

    Optional<Customer> getCustomerByEmail(String email);
}