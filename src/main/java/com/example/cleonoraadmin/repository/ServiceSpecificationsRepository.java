package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.CleaningSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceSpecificationsRepository extends JpaRepository<CleaningSpecifications, Long>, JpaSpecificationExecutor<CleaningSpecifications> {
}