package com.example.cleonoraadmin.service.imp;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.mapper.CleaningMapper;
import com.example.cleonoraadmin.model.TopCleaning;
import com.example.cleonoraadmin.model.cleaning.CleaningRequest;
import com.example.cleonoraadmin.model.cleaning.CleaningResponse;
import com.example.cleonoraadmin.repository.CleaningRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class CleaningServiceImpTest {

    @Mock
    private CleaningRepository cleaningRepository;

    @Mock
    private CleaningMapper cleaningMapper;

    @InjectMocks
    private CleaningServiceImp cleaningService;

    @Test
    void save_ShouldReturnSavedCleaning() {
        Cleaning cleaningToSave = createCleaning(1L, "Уборка 1");
        Cleaning savedCleaning = createCleaning(1L, "Уборка 1");
        when(cleaningRepository.save(cleaningToSave)).thenReturn(savedCleaning);

        Cleaning result = cleaningService.save(cleaningToSave);

        assertNotNull(result);
        assertEquals(savedCleaning, result);
        verify(cleaningRepository, times(1)).save(cleaningToSave);
    }

    @Test
    void getPageAllServices_WithSearch_ShouldReturnPageOfCleaningResponses() {
        int page = 0;
        int size = 10;
        String search = "Уборка";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Cleaning> cleaningPage = new PageImpl<>(List.of(createCleaning(1L, "Уборка 1"), createCleaning(2L, "Уборка 2")), pageRequest, 2);
        Page<CleaningResponse> cleaningResponsePage = new PageImpl<>(List.of(createCleaningResponse(1L, "Уборка 1"), createCleaningResponse(2L, "Уборка 2")), pageRequest, 2);

        when(cleaningRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(cleaningPage);
        when(cleaningMapper.toResponsePage(cleaningPage)).thenReturn(cleaningResponsePage);

        Page<CleaningResponse> resultPage = cleaningService.getPageAllServices(page, size, search);

        assertNotNull(resultPage);
        assertEquals(cleaningResponsePage, resultPage);

        verify(cleaningRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(cleaningMapper, times(1)).toResponsePage(cleaningPage);
    }




    @Test
    void getPageAllServices_WithoutSearch_ShouldReturnPageOfCleaningResponses() {
        int page = 0;
        int size = 10;
        String search = null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Cleaning> cleaningPage = new PageImpl<>(List.of(createCleaning(1L, "Уборка 1"), createCleaning(2L, "Уборка 2")), pageRequest, 2);
        Page<CleaningResponse> cleaningResponsePage = new PageImpl<>(List.of(createCleaningResponse(1L, "Уборка 1"), createCleaningResponse(2L, "Уборка 2")), pageRequest, 2);

        when(cleaningRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest))).thenReturn(cleaningPage);
        when(cleaningMapper.toResponsePage(cleaningPage)).thenReturn(cleaningResponsePage);

        Page<CleaningResponse> resultPage = cleaningService.getPageAllServices(page, size, search);

        assertNotNull(resultPage);
        assertEquals(cleaningResponsePage, resultPage);

        verify(cleaningRepository, times(1)).findAll(Mockito.any(Specification.class), Mockito.eq(pageRequest));
        verify(cleaningMapper, times(1)).toResponsePage(cleaningPage);
    }

    @Test
    void getServiceById_ServiceExists_ShouldReturnOptionalOfCleaning() {
        Long serviceId = 1L;
        Cleaning cleaning = createCleaning(serviceId, "Уборка 1");
        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.of(cleaning));

        Optional<Cleaning> result = cleaningService.getServiceById(serviceId);

        assertTrue(result.isPresent());
        assertEquals(cleaning, result.get());
        verify(cleaningRepository, times(1)).findById(serviceId);
    }

    @Test
    void getServiceById_ServiceNotFound_ShouldReturnEmptyOptional() {
        Long serviceId = 1L;
        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.empty());

        Optional<Cleaning> result = cleaningService.getServiceById(serviceId);

        assertTrue(result.isEmpty());
        verify(cleaningRepository, times(1)).findById(serviceId);
    }

    @Test
    void getServiceResponseById_ServiceExists_ShouldReturnCleaningResponse() {
        Long serviceId = 1L;
        Cleaning cleaning = createCleaning(serviceId, "Уборка 1");
        CleaningResponse cleaningResponse = createCleaningResponse(serviceId, "Уборка 1");
        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.of(cleaning));
        when(cleaningMapper.toResponse(cleaning)).thenReturn(cleaningResponse);

        CleaningResponse result = cleaningService.getServiceResponseById(serviceId);

        assertNotNull(result);
        assertEquals(cleaningResponse, result);
        verify(cleaningRepository, times(1)).findById(serviceId);
        verify(cleaningMapper, times(1)).toResponse(cleaning);
    }

    @Test
    void getServiceResponseById_ServiceNotFound_ShouldReturnNull() {
        Long serviceId = 1L;
        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.empty());
        when(cleaningMapper.toResponse(null)).thenReturn(null);

        CleaningResponse result = cleaningService.getServiceResponseById(serviceId);

        assertNull(result);
        verify(cleaningRepository, times(1)).findById(serviceId);
        verify(cleaningMapper, times(1)).toResponse(null);
    }

    @Test
    void saveNewService_SuccessfulSave_ShouldReturnCleaningResponse() {
        CleaningRequest cleaningRequest = createCleaningRequest("Уборка 1");
        Cleaning cleaningToSave = createCleaning(null, "Уборка 1");
        Cleaning savedCleaning = createCleaning(1L, "Уборка 1");
        CleaningResponse cleaningResponse = createCleaningResponse(1L, "Уборка 1");

        when(cleaningMapper.toEntity(cleaningRequest)).thenReturn(cleaningToSave);
        when(cleaningRepository.save(cleaningToSave)).thenReturn(savedCleaning);
        when(cleaningMapper.toResponse(savedCleaning)).thenReturn(cleaningResponse);

        CleaningResponse result = cleaningService.saveNewService(cleaningRequest);

        assertNotNull(result);
        assertEquals(cleaningResponse, result);
        verify(cleaningMapper, times(1)).toEntity(cleaningRequest);
        verify(cleaningRepository, times(1)).save(cleaningToSave);
        verify(cleaningMapper, times(1)).toResponse(savedCleaning);
    }

    @Test
    void saveNewService_ExceptionDuringSave_ShouldThrowRuntimeException() {
        CleaningRequest cleaningRequest = createCleaningRequest("Уборка 1");
        Cleaning cleaningToSave = createCleaning(null, "Уборка 1");

        when(cleaningMapper.toEntity(cleaningRequest)).thenReturn(cleaningToSave);
        when(cleaningRepository.save(cleaningToSave)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> cleaningService.saveNewService(cleaningRequest));

        verify(cleaningMapper, times(1)).toEntity(cleaningRequest);
        verify(cleaningRepository, times(1)).save(cleaningToSave);
        verifyNoMoreInteractions(cleaningMapper);
    }

    @Test
    void updateService_ServiceExists_ShouldReturnUpdatedCleaningResponse() {
        Long serviceId = 1L;
        CleaningRequest cleaningRequest = createCleaningRequest("Обновленная уборка");
        Cleaning existingCleaning = createCleaning(serviceId, "Уборка 1");
        Cleaning updatedCleaningEntity = createCleaning(serviceId, "Обновленная уборка");
        CleaningResponse updatedCleaningResponse = createCleaningResponse(serviceId, "Обновленная уборка");

        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.of(existingCleaning));
        when(cleaningMapper.partialUpdate(cleaningRequest, existingCleaning)).thenReturn(updatedCleaningEntity); // partialUpdate void метод, mock void методов по умолчанию ничего не делают
        when(cleaningRepository.save(existingCleaning)).thenReturn(updatedCleaningEntity);
        when(cleaningMapper.toResponse(updatedCleaningEntity)).thenReturn(updatedCleaningResponse);

        CleaningResponse result = cleaningService.updateService(serviceId, cleaningRequest);

        assertNotNull(result);
        assertEquals(updatedCleaningResponse, result);
        verify(cleaningRepository, times(1)).findById(serviceId);
        verify(cleaningMapper, times(1)).partialUpdate(cleaningRequest, existingCleaning);
        verify(cleaningRepository, times(1)).save(existingCleaning);
        verify(cleaningMapper, times(1)).toResponse(updatedCleaningEntity);
    }

    @Test
    void updateService_ServiceNotFound_ShouldThrowEntityNotFoundException() {
        Long serviceId = 1L;
        CleaningRequest cleaningRequest = createCleaningRequest("Обновленная уборка");
        when(cleaningRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cleaningService.updateService(serviceId, cleaningRequest));

        verify(cleaningRepository, times(1)).findById(serviceId);
        verifyNoMoreInteractions(cleaningMapper);
    }

    @Test
    void deleteServiceById_ServiceExists_SuccessfulDelete_ShouldReturnTrue() {
        Long serviceId = 1L;
        when(cleaningRepository.existsById(serviceId)).thenReturn(true);
        doNothing().when(cleaningRepository).deleteById(serviceId);

        boolean result = cleaningService.deleteServiceById(serviceId);

        assertTrue(result);
        verify(cleaningRepository, times(1)).existsById(serviceId);
        verify(cleaningRepository, times(1)).deleteById(serviceId);
    }

    @Test
    void deleteServiceById_ServiceExists_ExceptionDuringDelete_ShouldThrowRuntimeException() {
        Long serviceId = 1L;
        when(cleaningRepository.existsById(serviceId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(cleaningRepository).deleteById(serviceId);

        assertThrows(RuntimeException.class, () -> cleaningService.deleteServiceById(serviceId));

        verify(cleaningRepository, times(1)).existsById(serviceId);
        verify(cleaningRepository, times(1)).deleteById(serviceId);
    }


    @Test
    void deleteServiceById_ServiceNotFound_ShouldThrowEntityNotFoundException() {
        Long serviceId = 1L;
        when(cleaningRepository.existsById(serviceId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> cleaningService.deleteServiceById(serviceId));

        verify(cleaningRepository, times(1)).existsById(serviceId);
        verify(cleaningRepository, never()).deleteById(serviceId);
    }

    @Test
    void ifServiceMoreThan_CountMoreThanI_ShouldReturnTrue() {
        int i = 5;
        when(cleaningRepository.count()).thenReturn(10L);

        boolean result = cleaningService.ifServiceMoreThan(i);

        assertTrue(result);
        verify(cleaningRepository, times(1)).count();
    }

    @Test
    void ifServiceMoreThan_CountLessThanOrEqualI_ShouldReturnFalse() {
        int i = 10;
        when(cleaningRepository.count()).thenReturn(5L);

        boolean result = cleaningService.ifServiceMoreThan(i);

        assertFalse(result);
        verify(cleaningRepository, times(1)).count();
    }

    @Test
    void findTopCleaning_ShouldReturnEmptyList() {
        List<TopCleaning> result = cleaningService.findTopCleaning();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCleaning_CleaningExists_ShouldReturnCleaning() {
        Long cleaningId = 1L;
        Cleaning cleaning = createCleaning(cleaningId, "Уборка 1");
        when(cleaningRepository.findById(cleaningId)).thenReturn(Optional.of(cleaning));

        Cleaning result = cleaningService.getCleaning(cleaningId);

        assertNotNull(result);
        assertEquals(cleaning, result);
        verify(cleaningRepository, times(1)).findById(cleaningId);
    }

    @Test
    void getCleaning_CleaningNotFound_ShouldReturnNull() {
        Long cleaningId = 1L;
        when(cleaningRepository.findById(cleaningId)).thenReturn(Optional.empty());

        Cleaning result = cleaningService.getCleaning(cleaningId);

        assertNull(result);
        verify(cleaningRepository, times(1)).findById(cleaningId);
    }

    private Cleaning createCleaning(Long id, String name) {
        Cleaning cleaning = new Cleaning();
        cleaning.setId(id);
        cleaning.setName(name);
        cleaning.setDescription("Описание " + name);
        cleaning.setCategory(createCategory(1L, "Категория 1"));
        cleaning.setCleaningSpecifications(createCleaningSpecifications(1L, "Спецификация 1"));
        return cleaning;
    }

    private CleaningRequest createCleaningRequest(String name) {
        CleaningRequest cleaningRequest = new CleaningRequest();
        cleaningRequest.setName(name);
        cleaningRequest.setDescription("Описание " + name);
        cleaningRequest.setCategoryId(1L);
        cleaningRequest.setServiceSpecificationsId(1L);
        return cleaningRequest;
    }

    private CleaningResponse createCleaningResponse(Long id, String name) {
        CleaningResponse cleaningResponse = new CleaningResponse();
        cleaningResponse.setId(id);
        cleaningResponse.setName(name);
        cleaningResponse.setDescription("Описание " + name);
        cleaningResponse.setCategoryId(1L);
        cleaningResponse.setCategoryName("Категория 1");
        cleaningResponse.setServiceSpecificationsId(1L);
        cleaningResponse.setServiceSpecificationsName("Спецификация 1");
        return cleaningResponse;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private CleaningSpecifications createCleaningSpecifications(Long id, String name) {
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(id);
        cleaningSpecifications.setName(name);
        return cleaningSpecifications;
    }
}
