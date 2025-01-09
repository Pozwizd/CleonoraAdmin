package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.UploadFile;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.mapper.CleaningSpecificationsMapper;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsResponse;
import com.example.cleonoraadmin.repository.ServiceSpecificationsRepository;
import com.example.cleonoraadmin.service.CleaningSpecificationsService;
import com.example.cleonoraadmin.specification.CleaningSpecificationsSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CleaningSpecificationsImp implements CleaningSpecificationsService {

    private final ServiceSpecificationsRepository serviceSpecificationsRepository;
    private final CleaningSpecificationsMapper serviceSpecificationsMapper;
    private final UploadFile uploadFile;

    @Override
    public CleaningSpecifications save(CleaningSpecifications cleaningSpecifications) {
        return serviceSpecificationsRepository.save(cleaningSpecifications);
    }

    @Override
    public List<ServiceSpecificationsResponse> getAllServiceSpecifications() {
        return serviceSpecificationsRepository.findAll().stream()
                .map(serviceSpecificationsMapper::toResponse)
                .sorted(Comparator.comparing(ServiceSpecificationsResponse::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ServiceSpecificationsResponse> getAllServiceSpecifications(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<CleaningSpecifications> specifications = serviceSpecificationsRepository.findAll(
                CleaningSpecificationsSpec.search(search),
                pageRequest
        );
        return serviceSpecificationsMapper.toResponsePage(specifications);
    }


    @Override
    public Optional<CleaningSpecifications> getServiceSpecificationsById(Long id) {
        return serviceSpecificationsRepository.findById(id);
    }

    /**
     * Retrieve a ServiceSpecificationsResponse by its ID.
     *
     * @param id the ID of the service specification
     * @return the ServiceSpecificationsResponse corresponding to the given ID
     */
    @Override
    public ServiceSpecificationsResponse getServiceSpecificationsResponseById(Long id) {
        return serviceSpecificationsMapper.toResponse(serviceSpecificationsRepository.findById(id).orElse(null));
    }

    @Override
    public ServiceSpecificationsResponse saveNewServiceSpecifications(ServiceSpecificationsRequest serviceSpecificationsRequest) {
        return serviceSpecificationsMapper.toResponse(serviceSpecificationsRepository.save(serviceSpecificationsMapper.toEntity(serviceSpecificationsRequest, uploadFile)));
    }

    @Override
    public ServiceSpecificationsResponse updateServiceSpecifications(Long id, ServiceSpecificationsRequest serviceSpecificationsRequest) {
        return serviceSpecificationsRepository.findById(id)
                .map(serviceSpecifications -> {
                    CleaningSpecifications updatedSpecification = serviceSpecificationsMapper
                            .toEntity(serviceSpecificationsRequest, uploadFile);

                    updatedSpecification.setId(id);
                    serviceSpecificationsRepository.save(updatedSpecification);
                    return serviceSpecificationsMapper.toResponse(updatedSpecification);
                }).orElse(null);
    }

    @Override
    public boolean deleteServiceSpecificationsById(Long id) {
        try {
            serviceSpecificationsRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при удалении спецификации: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean ifServiceSpecificationsMoreThan(int i) {
        return serviceSpecificationsRepository.count() > i;
    }
}
