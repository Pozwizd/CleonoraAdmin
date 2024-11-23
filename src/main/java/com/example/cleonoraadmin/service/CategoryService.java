package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.model.category.CategoryRequest;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.Optional;



public interface CategoryService {


    Category save(Category category);
    Page<CategoryResponse> getPageAllCategories(int page, Integer size, String search);

    Optional<Category> getCategoryById(Long id);

    CategoryResponse saveNewCategory(@Valid CategoryRequest categoryRequest);

    Object updateCategory(Long id, @Valid CategoryRequest categoryRequest);

    boolean deleteCategoryById(Long id);

    boolean ifCategoryMoreThan(int i);
}
