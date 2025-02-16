package com.example.cleonoraadmin.service.imp;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cleonoraadmin.UploadFile;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.mapper.CleaningSpecificationsMapper;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsResponse;
import com.example.cleonoraadmin.repository.ServiceSpecificationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class CleaningSpecificationsImpTest {

    @Mock
    private ServiceSpecificationsRepository serviceSpecificationsRepository;

    @Mock
    private CleaningSpecificationsMapper serviceSpecificationsMapper;

    @Mock
    private UploadFile uploadFile;

    @InjectMocks
    private CleaningSpecificationsImp cleaningSpecificationsService;

    @Test
    void save_ShouldReturnSavedCleaningSpecifications() {
        CleaningSpecifications specificationsToSave = createCleaningSpecifications(1L, "Спецификация 1");
        CleaningSpecifications savedSpecifications = createCleaningSpecifications(1L, "Спецификация 1");
        when(serviceSpecificationsRepository.save(specificationsToSave)).thenReturn(savedSpecifications);

        CleaningSpecifications result = cleaningSpecificationsService.save(specificationsToSave);

        assertNotNull(result);
        assertEquals(savedSpecifications, result);
        verify(serviceSpecificationsRepository, times(1)).save(specificationsToSave);
    }

    @Test
    void getAllServiceSpecifications_ShouldReturnSortedListOfServiceSpecificationsResponses() {
        List<CleaningSpecifications> specificationsList = List.of(
                createCleaningSpecifications(2L, "Спецификация 2"),
                createCleaningSpecifications(1L, "Спецификация 1")
        );
        List<ServiceSpecificationsResponse> responseList = List.of(
                createServiceSpecificationsResponse(1L, "Спецификация 1"),
                createServiceSpecificationsResponse(2L, "Спецификация 2")
        );

        when(serviceSpecificationsRepository.findAll()).thenReturn(specificationsList);
        when(serviceSpecificationsMapper.toResponse(any(CleaningSpecifications.class)))
                .thenReturn(responseList.get(1)) // for spec 2
                .thenReturn(responseList.get(0)); // for spec 1

        List<ServiceSpecificationsResponse> result = cleaningSpecificationsService.getAllServiceSpecifications();

        assertNotNull(result);
        assertEquals(responseList, result);
        assertEquals(responseList.get(0).getId(), result.get(0).getId()); // Check order
        assertEquals(responseList.get(1).getId(), result.get(1).getId()); // Check order
        verify(serviceSpecificationsRepository, times(1)).findAll();
        verify(serviceSpecificationsMapper, times(2)).toResponse(any(CleaningSpecifications.class));
    }

    @Test
    void getAllServiceSpecifications_Paged_WithSearch_ShouldReturnPageOfServiceSpecificationsResponses() {
        int page = 0;
        int size = 10;
        String search = "Спецификация";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<CleaningSpecifications> specificationsPage = new PageImpl<>(List.of(createCleaningSpecifications(1L, "Спецификация 1"), createCleaningSpecifications(2L, "Спецификация 2")), pageRequest, 2);
        Page<ServiceSpecificationsResponse> responsePage = new PageImpl<>(List.of(createServiceSpecificationsResponse(1L, "Спецификация 1"), createServiceSpecificationsResponse(2L, "Спецификация 2")), pageRequest, 2);

        when(serviceSpecificationsRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(specificationsPage);
        when(serviceSpecificationsMapper.toResponsePage(specificationsPage)).thenReturn(responsePage);

        Page<ServiceSpecificationsResponse> resultPage = cleaningSpecificationsService.getAllServiceSpecifications(page, size, search);

        assertNotNull(resultPage);
        assertEquals(responsePage, resultPage);
        verify(serviceSpecificationsRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(serviceSpecificationsMapper, times(1)).toResponsePage(specificationsPage);
    }

    @Test
    void getAllServiceSpecifications_Paged_WithoutSearch_ShouldReturnPageOfServiceSpecificationsResponses() {
        int page = 0;
        int size = 10;
        String search = null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<CleaningSpecifications> specificationsPage = new PageImpl<>(List.of(createCleaningSpecifications(1L, "Спецификация 1"), createCleaningSpecifications(2L, "Спецификация 2")), pageRequest, 2);
        Page<ServiceSpecificationsResponse> responsePage = new PageImpl<>(List.of(createServiceSpecificationsResponse(1L, "Спецификация 1"), createServiceSpecificationsResponse(2L, "Спецификация 2")), pageRequest, 2);

        when(serviceSpecificationsRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(specificationsPage);
        when(serviceSpecificationsMapper.toResponsePage(specificationsPage)).thenReturn(responsePage);

        Page<ServiceSpecificationsResponse> resultPage = cleaningSpecificationsService.getAllServiceSpecifications(page, size, search);

        assertNotNull(resultPage);
        assertEquals(responsePage, resultPage);
        verify(serviceSpecificationsRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(serviceSpecificationsMapper, times(1)).toResponsePage(specificationsPage);
    }

    @Test
    void getServiceSpecificationsById_SpecificationsExists_ShouldReturnOptionalOfCleaningSpecifications() {
        Long specificationsId = 1L;
        CleaningSpecifications specifications = createCleaningSpecifications(specificationsId, "Спецификация 1");
        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.of(specifications));

        Optional<CleaningSpecifications> result = cleaningSpecificationsService.getServiceSpecificationsById(specificationsId);

        assertTrue(result.isPresent());
        assertEquals(specifications, result.get());
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
    }

    @Test
    void getServiceSpecificationsById_SpecificationsNotFound_ShouldReturnEmptyOptional() {
        Long specificationsId = 1L;
        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.empty());

        Optional<CleaningSpecifications> result = cleaningSpecificationsService.getServiceSpecificationsById(specificationsId);

        assertTrue(result.isEmpty());
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
    }

    @Test
    void getServiceSpecificationsResponseById_SpecificationsExists_ShouldReturnServiceSpecificationsResponse() {
        Long specificationsId = 1L;
        CleaningSpecifications specifications = createCleaningSpecifications(specificationsId, "Спецификация 1");
        ServiceSpecificationsResponse response = createServiceSpecificationsResponse(specificationsId, "Спецификация 1");
        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.of(specifications));
        when(serviceSpecificationsMapper.toResponse(specifications)).thenReturn(response);

        ServiceSpecificationsResponse result = cleaningSpecificationsService.getServiceSpecificationsResponseById(specificationsId);

        assertNotNull(result);
        assertEquals(response, result);
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
        verify(serviceSpecificationsMapper, times(1)).toResponse(specifications);
    }

    @Test
    void getServiceSpecificationsResponseById_SpecificationsNotFound_ShouldReturnNull() {
        Long specificationsId = 1L;
        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.empty());
        when(serviceSpecificationsMapper.toResponse(null)).thenReturn(null); // Mock mapper for null input

        ServiceSpecificationsResponse result = cleaningSpecificationsService.getServiceSpecificationsResponseById(specificationsId);

        assertNull(result);
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
        verify(serviceSpecificationsMapper, times(1)).toResponse(null);
    }

    @Test
    void saveNewServiceSpecifications_ShouldReturnServiceSpecificationsResponse() {
        ServiceSpecificationsRequest request = createServiceSpecificationsRequest("Спецификация 1");
        CleaningSpecifications specificationsToSave = createCleaningSpecifications(null, "Спецификация 1");
        CleaningSpecifications savedSpecifications = createCleaningSpecifications(1L, "Спецификация 1");
        ServiceSpecificationsResponse response = createServiceSpecificationsResponse(1L, "Спецификация 1");

        when(serviceSpecificationsMapper.toEntity(request, uploadFile)).thenReturn(specificationsToSave);
        when(serviceSpecificationsRepository.save(specificationsToSave)).thenReturn(savedSpecifications);
        when(serviceSpecificationsMapper.toResponse(savedSpecifications)).thenReturn(response);

        ServiceSpecificationsResponse result = cleaningSpecificationsService.saveNewServiceSpecifications(request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(serviceSpecificationsMapper, times(1)).toEntity(request, uploadFile);
        verify(serviceSpecificationsRepository, times(1)).save(specificationsToSave);
        verify(serviceSpecificationsMapper, times(1)).toResponse(savedSpecifications);
    }

    @Test
    void updateServiceSpecifications_SpecificationsExists_ShouldReturnUpdatedServiceSpecificationsResponse() {
        Long specificationsId = 1L;
        ServiceSpecificationsRequest request = createServiceSpecificationsRequest("Обновленная спецификация");
        CleaningSpecifications existingSpecifications = createCleaningSpecifications(specificationsId, "Спецификация 1");
        CleaningSpecifications updatedSpecificationsEntity = createCleaningSpecifications(specificationsId, "Обновленная спецификация");
        ServiceSpecificationsResponse updatedResponse = createServiceSpecificationsResponse(specificationsId, "Обновленная спецификация");

        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.of(existingSpecifications));
        when(serviceSpecificationsMapper.toEntity(request, uploadFile)).thenReturn(updatedSpecificationsEntity);
        when(serviceSpecificationsRepository.save(updatedSpecificationsEntity)).thenReturn(updatedSpecificationsEntity);
        when(serviceSpecificationsMapper.toResponse(updatedSpecificationsEntity)).thenReturn(updatedResponse);

        ServiceSpecificationsResponse result = cleaningSpecificationsService.updateServiceSpecifications(specificationsId, request);

        assertNotNull(result);
        assertEquals(updatedResponse, result);
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
        verify(serviceSpecificationsMapper, times(1)).toEntity(request, uploadFile);
        verify(serviceSpecificationsRepository, times(1)).save(updatedSpecificationsEntity);
        verify(serviceSpecificationsMapper, times(1)).toResponse(updatedSpecificationsEntity);
    }

    @Test
    void updateServiceSpecifications_SpecificationsNotFound_ShouldReturnNull() {
        Long specificationsId = 1L;
        ServiceSpecificationsRequest request = createServiceSpecificationsRequest("Обновленная спецификация");
        when(serviceSpecificationsRepository.findById(specificationsId)).thenReturn(Optional.empty());

        ServiceSpecificationsResponse result = cleaningSpecificationsService.updateServiceSpecifications(specificationsId, request);

        assertNull(result);
        verify(serviceSpecificationsRepository, times(1)).findById(specificationsId);
        verifyNoMoreInteractions(serviceSpecificationsMapper);
        verifyNoMoreInteractions(uploadFile);
    }

    @Test
    void deleteServiceSpecificationsById_SuccessfulDelete_ShouldReturnTrue() {
        Long specificationsId = 1L;
        doNothing().when(serviceSpecificationsRepository).deleteById(specificationsId);

        boolean result = cleaningSpecificationsService.deleteServiceSpecificationsById(specificationsId);

        assertTrue(result);
        verify(serviceSpecificationsRepository, times(1)).deleteById(specificationsId);
    }

    @Test
    void deleteServiceSpecificationsById_ExceptionDuringDelete_ShouldReturnFalse() {
        Long specificationsId = 1L;
        doThrow(new RuntimeException("Database error")).when(serviceSpecificationsRepository).deleteById(specificationsId);

        boolean result = cleaningSpecificationsService.deleteServiceSpecificationsById(specificationsId);

        assertFalse(result);
        verify(serviceSpecificationsRepository, times(1)).deleteById(specificationsId);
    }

    @Test
    void ifServiceSpecificationsMoreThan_CountMoreThanI_ShouldReturnTrue() {
        int i = 5;
        when(serviceSpecificationsRepository.count()).thenReturn(10L);

        boolean result = cleaningSpecificationsService.ifServiceSpecificationsMoreThan(i);

        assertTrue(result);
        verify(serviceSpecificationsRepository, times(1)).count();
    }

    @Test
    void ifServiceSpecificationsMoreThan_CountLessThanOrEqualI_ShouldReturnFalse() {
        int i = 10;
        when(serviceSpecificationsRepository.count()).thenReturn(5L);

        boolean result = cleaningSpecificationsService.ifServiceSpecificationsMoreThan(i);

        assertFalse(result);
        verify(serviceSpecificationsRepository, times(1)).count();
    }


    // Helper methods for creating test data
    private CleaningSpecifications createCleaningSpecifications(Long id, String name) {
        CleaningSpecifications specifications = new CleaningSpecifications();
        specifications.setId(id);
        specifications.setName(name);
        return specifications;
    }

    private ServiceSpecificationsRequest createServiceSpecificationsRequest(String name) {
        ServiceSpecificationsRequest request = new ServiceSpecificationsRequest();
        request.setName(name);
        return request;
    }

    private ServiceSpecificationsResponse createServiceSpecificationsResponse(Long id, String name) {
        ServiceSpecificationsResponse response = new ServiceSpecificationsResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }
}