package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.Workday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkdayRepository extends JpaRepository<Workday, Long>, JpaSpecificationExecutor<Workday> {
    Optional<Workday> findByDate(LocalDate currentDate);
}