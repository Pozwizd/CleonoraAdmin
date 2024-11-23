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

    @GetMapping("/getAllCategories")
    public @ResponseBody Page<CategoryResponse> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "") String search,
                                                                 @RequestParam(defaultValue = "5") Integer size) {
        return categoryService.getPageAllCategories(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getCategory(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse createdCategory = categoryService.saveNewCategory(categoryRequest);
            return ResponseEntity.ok(createdCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при создании категории: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Дублирующаяся запись. Категория с таким именем уже существует.");
        } catch (Exception e) {
            log.error("Ошибка при создании категории: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            Optional<Category> existingCategory = categoryService.getCategoryById(id);
            if (existingCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest));
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при обновлении категории: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение уникальности данных.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении категории: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
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
