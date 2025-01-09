package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.Cleaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CleaningRepository extends JpaRepository<Cleaning, Long>, JpaSpecificationExecutor<Cleaning> {
}