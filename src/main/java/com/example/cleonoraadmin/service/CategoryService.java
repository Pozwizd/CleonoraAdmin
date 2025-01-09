package com.example.cleonoraadmin.service;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.model.category.CategoryRequest;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;



public interface CategoryService {


    Category save(Category category);
    List<CategoryResponse > getAllCategories();
    Page<CategoryResponse> getPageAllCategories(int page, Integer size, String search);

    Optional<Category> getCategoryById(Long id);

    CategoryResponse getCategoryResponseById(Long id);

    CategoryResponse saveNewCategory(@Valid CategoryRequest categoryRequest);

    CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest);

    boolean deleteCategoryById(Long id);

    boolean ifCategoryMoreThan(int i);
}
