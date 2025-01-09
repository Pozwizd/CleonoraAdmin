package com.example.cleonoraadmin.controller;


import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.model.category.CategoryRequest;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import com.example.cleonoraadmin.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/category")
@AllArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "category");
        model.addAttribute("title", "Категории");
    }

    @GetMapping({"", "/"})
    public ModelAndView getCategoryPage() {
        return new ModelAndView("category/categoriesPage");
    }


    @GetMapping({"/getAllCategoriesList"})
    public @ResponseBody List<CategoryResponse> getAllCategoryList() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/getAllCategories")
    public @ResponseBody Page<CategoryResponse> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "") String search,
                                                                 @RequestParam(defaultValue = "5") Integer size) {
        return categoryService.getPageAllCategories(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getCategory(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryResponseById(id);

        return ResponseEntity.ok(category);
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.saveNewCategory(categoryRequest);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Optional<Category> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            boolean deleted = categoryService.deleteCategoryById(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении категории: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }
}
