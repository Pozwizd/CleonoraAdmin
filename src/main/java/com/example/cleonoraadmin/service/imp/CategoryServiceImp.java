package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.mapper.CategoryMapper;
import com.example.cleonoraadmin.model.category.CategoryRequest;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import com.example.cleonoraadmin.repository.CategoryRepository;
import com.example.cleonoraadmin.service.CategoryService;
import com.example.cleonoraadmin.specification.CategorySpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Page<CategoryResponse> getPageAllCategories(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryMapper.toResponsePage(categoryRepository.findAll(CategorySpecification.search(search), pageRequest));
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public CategoryResponse saveNewCategory(CategoryRequest categoryRequest) {
        try {
            Category category = categoryMapper.toEntity(categoryRequest);
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toResponse(savedCategory);
        } catch (Exception e) {
            log.error("Ошибка при сохранении новой категории: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении новой категории", e);
        }
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new EntityNotFoundException("Категория с ID " + id + " не найдена");
        }
        Category categoryToUpdate = existingCategory.get();
        categoryMapper.partialUpdate(categoryRequest, categoryToUpdate);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public boolean deleteCategoryById(Long id) {
        if (categoryRepository.existsById(id)) {
            try {
                categoryRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                log.error("Ошибка при удалении категории с ID {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Ошибка при удалении категории", e);
            }
        } else {
            throw new EntityNotFoundException("Категория с ID " + id + " не найдена");
        }
    }

    @Override
    public boolean ifCategoryMoreThan(int i) {
        return categoryRepository.count() > i;
    }
}
