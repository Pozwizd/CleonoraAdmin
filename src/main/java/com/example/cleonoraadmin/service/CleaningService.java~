package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.model.TopCleaning;
import com.example.cleonoraadmin.model.cleaning.CleaningRequest;
import com.example.cleonoraadmin.model.cleaning.CleaningResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CleaningService {

    Cleaning save(Cleaning cleaning);
    Page<CleaningResponse> getPageAllServices(int page, Integer size, String search);

    Optional<Cleaning> getServiceById(Long id);

    CleaningResponse getServiceResponseById(Long id);

    CleaningResponse saveNewService(@Valid CleaningRequest cleaningRequest);

    CleaningResponse updateService(Long id, @Valid CleaningRequest cleaningRequest);

    boolean deleteServiceById(Long id);

    boolean ifServiceMoreThan(int i);

    List<TopCleaning> findTopCleaning();

}
