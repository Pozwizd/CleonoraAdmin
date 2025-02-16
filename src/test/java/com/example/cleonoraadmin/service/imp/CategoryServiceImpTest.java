package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.mapper.CategoryMapper;
import com.example.cleonoraadmin.model.category.CategoryRequest;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import com.example.cleonoraadmin.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImp categoryService;

    @Test
    void testSave() {
        Category category = new Category();
        category.setName("TestCategory");

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.save(category);

        assertNotNull(result);
        assertEquals("TestCategory", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");
        category1.setDescription("Description1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");
        category2.setDescription("Description2");

        List<Category> categories = Arrays.asList(category1, category2);

        CategoryResponse response1 = new CategoryResponse();
        response1.setId(1L);
        response1.setName("Category1");
        response1.setDescription("Description1");

        CategoryResponse response2 = new CategoryResponse();
        response2.setId(2L);
        response2.setName("Category2");
        response2.setDescription("Description2");

        List<CategoryResponse> responses = Arrays.asList(response1, response2);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseList(categories)).thenReturn(responses);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category1", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toResponseList(categories);
    }
    @Test
    void testGetPageAllCategories() {
        int page = 0;
        int size = 10;
        String search = "test";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");
        category1.setDescription("Description1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");
        category2.setDescription("Description2");

        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageRequest, categories.size());

        CategoryResponse response1 = new CategoryResponse();
        response1.setId(1L);
        response1.setName("Category1");
        response1.setDescription("Description1");

        CategoryResponse response2 = new CategoryResponse();
        response2.setId(2L);
        response2.setName("Category2");
        response2.setDescription("Description2");

        List<CategoryResponse> responses = Arrays.asList(response1, response2);
        Page<CategoryResponse> responsePage = new PageImpl<>(responses, pageRequest, responses.size());

        when(categoryRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(categoryPage);
        when(categoryMapper.toResponsePage(categoryPage)).thenReturn(responsePage);

        Page<CategoryResponse> result = categoryService.getPageAllCategories(page, size, search);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Category1", result.getContent().get(0).getName());
        verify(categoryRepository, times(1)).findAll(any(Specification.class), eq(pageRequest));
        verify(categoryMapper, times(1)).toResponsePage(categoryPage);
    }

    @Test
    void testGetCategoryById_CategoryFound() {
        Long id = 1L;

        Category mockCategory = new Category();
        mockCategory.setId(id);
        mockCategory.setName("Category1");
        mockCategory.setDescription("Description1");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(mockCategory));

        Optional<Category> result = categoryService.getCategoryById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testGetCategoryById_CategoryNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(id);

        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(id);
    }



    @Test
    void testGetCategoryResponseById_CategoryFound() {
        Long id = 1L;

        Category mockCategory = new Category();
        mockCategory.setId(id);
        mockCategory.setName("Category1");
        mockCategory.setDescription("Description1");

        CategoryResponse mockResponse = new CategoryResponse();
        mockResponse.setId(id);
        mockResponse.setName("Category1");
        mockResponse.setDescription("Description1");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(mockCategory));
        when(categoryMapper.toResponse(mockCategory)).thenReturn(mockResponse);

        CategoryResponse result = categoryService.getCategoryResponseById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toResponse(mockCategory);
    }

    @Test
    void testGetCategoryResponseById_CategoryNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        CategoryResponse result = categoryService.getCategoryResponseById(id);

        assertNull(result);

        verify(categoryRepository, times(1)).findById(id);

        verify(categoryMapper, never()).toResponse(any());
    }


    @Test
    void testSaveNewCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("NewCategory");
        request.setDescription("New Description");

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("NewCategory");
        savedCategory.setDescription("New Description");

        CategoryResponse mockResponse = new CategoryResponse();
        mockResponse.setId(1L);
        mockResponse.setName("NewCategory");
        mockResponse.setDescription("New Description");

        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(mockResponse);

        CategoryResponse result = categoryService.saveNewCategory(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("NewCategory", result.getName());
        verify(categoryMapper, times(1)).toEntity(request);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toResponse(savedCategory);
    }

    @Test
    void testSaveNewCategory_Exception() {
        CategoryRequest request = new CategoryRequest();
        request.setName("NewCategory");
        request.setDescription("New Description");

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.saveNewCategory(request));
        assertEquals("Ошибка при сохранении новой категории", exception.getMessage());

        verify(categoryMapper, times(1)).toEntity(request);
        verify(categoryRepository, times(1)).save(category);
    }
    @Test
    void testUpdateCategory_Success() {
        Long id = 1L;

        // Создаем объекты через пустой конструктор и сеттеры
        CategoryRequest request = new CategoryRequest();
        request.setName("UpdatedCategory");
        request.setDescription("Updated Description");

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("OldCategory");
        existingCategory.setDescription("Old Description");

        // Обновляемый объект (после partialUpdate)
        Category updatedCategory = new Category();
        updatedCategory.setId(id);
        updatedCategory.setName("UpdatedCategory");
        updatedCategory.setDescription("Updated Description");

        CategoryResponse mockResponse = new CategoryResponse();
        mockResponse.setId(id);
        mockResponse.setName("UpdatedCategory");
        mockResponse.setDescription("Updated Description");

        // Мокируем репозиторий и маппер
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.partialUpdate(request, existingCategory)).thenReturn(updatedCategory); // partialUpdate возвращает значение
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory); // Сохраняем existingCategory
        when(categoryMapper.toResponse(updatedCategory)).thenReturn(mockResponse);

        // Вызываем метод
        CategoryResponse result = categoryService.updateCategory(id, request);

        // Проверяем результат
        assertNotNull(result);
        assertEquals("UpdatedCategory", result.getName());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).partialUpdate(request, existingCategory);
        verify(categoryRepository, times(1)).save(existingCategory); // Проверяем сохранение existingCategory
        verify(categoryMapper, times(1)).toResponse(updatedCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        Long id = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("UpdatedCategory");
        request.setDescription("Updated Description");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(id, request));
        assertEquals("Категория с ID 1 не найдена", exception.getMessage());

        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteCategoryById_Success() {
        Long id = 1L;

        when(categoryRepository.existsById(id)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(id);

        boolean result = categoryService.deleteCategoryById(id);

        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(id);
        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCategoryById_NotFound() {
        Long id = 1L;

        when(categoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategoryById(id));
        assertEquals("Категория с ID 1 не найдена", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(id);
    }

    @Test
    void testDeleteCategoryById_Exception() {
        Long id = 1L;

        when(categoryRepository.existsById(id)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(categoryRepository).deleteById(id);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.deleteCategoryById(id));
        assertEquals("Ошибка при удалении категории", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(id);
        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void testIfCategoryMoreThan_True() {
        long count = 5L;
        int threshold = 3;

        when(categoryRepository.count()).thenReturn(count);

        boolean result = categoryService.ifCategoryMoreThan(threshold);

        assertTrue(result);
        verify(categoryRepository, times(1)).count();
    }

    @Test
    void testIfCategoryMoreThan_False() {
        long count = 2L;
        int threshold = 3;

        when(categoryRepository.count()).thenReturn(count);

        boolean result = categoryService.ifCategoryMoreThan(threshold);

        assertFalse(result);
        verify(categoryRepository, times(1)).count();
    }
}