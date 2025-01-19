package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;


public interface CleaningSpecificationsService {

    CleaningSpecifications save(CleaningSpecifications cleaningSpecifications);

    List<ServiceSpecificationsResponse> getAllServiceSpecifications();
    Page<ServiceSpecificationsResponse> getAllServiceSpecifications(int page, Integer size, String search);

    Optional<CleaningSpecifications> getServiceSpecificationsById(Long id);

    ServiceSpecificationsResponse getServiceSpecificationsResponseById(Long id);

    ServiceSpecificationsResponse saveNewServiceSpecifications(ServiceSpecificationsRequest serviceSpecificationsRequest);

    ServiceSpecificationsResponse updateServiceSpecifications(Long id, ServiceSpecificationsRequest serviceSpecificationsRequest);

    boolean deleteServiceSpecificationsById(Long id);

    boolean ifServiceSpecificationsMoreThan(int i);
}
