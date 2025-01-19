package com.example.cleonoraadmin.service.imp;


import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.mapper.CleaningMapper;
import com.example.cleonoraadmin.model.TopCleaning;
import com.example.cleonoraadmin.model.cleaning.CleaningRequest;
import com.example.cleonoraadmin.model.cleaning.CleaningResponse;
import com.example.cleonoraadmin.repository.CleaningRepository;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.specification.CleaningSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CleaningServiceImp implements CleaningService {

    private final CleaningRepository cleaningRepository;
    private final CleaningMapper cleaningMapper;

    @Override
    public Cleaning save(Cleaning cleaning) {
        return cleaningRepository.save(cleaning);
    }

    @Override
    public Page<CleaningResponse> getPageAllServices(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return cleaningMapper.toResponsePage(cleaningRepository.findAll(CleaningSpecification.search(search), pageRequest));
    }

    @Override
    public Cleaning getCleaning(Long id) {
        return cleaningRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Cleaning> getServiceById(Long id) {
        return cleaningRepository.findById(id);
    }

    @Override
    public CleaningResponse getServiceResponseById(Long id) {
        return cleaningMapper.toResponse(cleaningRepository.findById(id).orElse(null));
    }

    @Override
    public CleaningResponse saveNewService(CleaningRequest cleaningRequest) {
        try {
            // Преобразование в сущность и сохранение
            Cleaning cleaning = cleaningMapper.toEntity(cleaningRequest);
            Cleaning savedCleaning = cleaningRepository.save(cleaning);
            return cleaningMapper.toResponse(savedCleaning);
        } catch (Exception e) {
            log.error("Ошибка при сохранении новой услуги: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении новой услуги", e);
        }
    }

    @Override
    public CleaningResponse updateService(Long id, CleaningRequest cleaningRequest) {
        Optional<Cleaning> existingService = cleaningRepository.findById(id);
        if (existingService.isEmpty()) {
            throw new EntityNotFoundException("Услуга с ID " + id + " не найдена");
        }
        Cleaning cleaningToUpdate = existingService.get();
        cleaningMapper.partialUpdate(cleaningRequest, cleaningToUpdate);
        Cleaning updatedCleaning = cleaningRepository.save(cleaningToUpdate);
        return cleaningMapper.toResponse(updatedCleaning);
    }

    @Override
    public boolean deleteServiceById(Long id) {
        if (cleaningRepository.existsById(id)) {
            try {
                cleaningRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                log.error("Ошибка при удалении услуги с ID {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Ошибка при удалении услуги", e);
            }
        } else {
            throw new EntityNotFoundException("Услуга с ID " + id + " не найдена");
        }
    }

    @Override
    public boolean ifServiceMoreThan(int i) {
        return cleaningRepository.count() > i;
    }

    @Override
    public List<TopCleaning> findTopCleaning() {
        return List.of();
    }
}
