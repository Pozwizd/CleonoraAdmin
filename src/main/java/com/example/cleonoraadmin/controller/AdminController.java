package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.AdminRole;
import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.AdminUserRequest;
import com.example.cleonoraadmin.model.AdminUserResponse;
import com.example.cleonoraadmin.service.AdminUserService;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final AdminUserService adminUserService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "adminUsers");
        model.addAttribute("title", "Персонал");
    }

    @GetMapping({"", "/"})
    public ModelAndView admin() {
        return new ModelAndView("user/usersPage");
        }

    @GetMapping("/getAll")
    public @ResponseBody Page<AdminUserResponse> getAllAdminUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "") String search,
                                       @RequestParam(defaultValue = "5") Integer size) {
        return adminUserService.getPageAllAdminUsers(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getAdminUser(@PathVariable Long id) {
        Optional<AdminUser> user = adminUserService.getAdminUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminUserRequest adminUserRequest) {
        try {
            AdminUserResponse createdUser = adminUserService.saveNewUserFromRequest(adminUserRequest);
            return ResponseEntity.ok(createdUser); // Возвращаем созданный объект пользователя
        } catch (DataIntegrityViolationException e) {
            // Обработка нарушений ограничений базы данных (например, уникальный email)
            log.error("Ошибка базы данных при создании пользователя: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Дублирующаяся запись. Пользователь с таким email уже существует.");
        } catch (Exception e) {
            // Обработка других исключений
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable Long id, @Valid @RequestBody AdminUserRequest adminUserRequest) {
        try {
            Optional<AdminUser> existingUser = adminUserService.getAdminUserById(id);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            return ResponseEntity.ok(adminUserService.updateAdminUser(id, adminUserRequest)); // Возвращаем обновленного пользователя

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при обновлении пользователя: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение уникальности данных.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable Long id) {
        try {
            boolean deleted = adminUserService.deleteAdminUserById(id); // Предполагается наличие метода deleteAdminUserById в сервисе
            if (deleted) {
                return ResponseEntity.ok().build(); // 204 No Content - успешное удаление
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found - пользователь не найден
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }


    @GetMapping("/roles")
    public @ResponseBody List<AdminRole> getRole() {
        return List.of(AdminRole.values());
    }

}
