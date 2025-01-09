package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.OrderCleaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderCleaningRepository extends JpaRepository<OrderCleaning, Long>, JpaSpecificationExecutor<OrderCleaning> {

}