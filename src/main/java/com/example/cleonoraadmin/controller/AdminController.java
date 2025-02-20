package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.AdminRole;
import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.admin.AdminUserResponse;
import com.example.cleonoraadmin.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    @GetMapping("/getAllManagerUsers")
    public @ResponseBody Page<AdminUserResponse> getAllAdminUsers(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "") String search,
                                                                  @RequestParam(defaultValue = "5") Integer size,
                                                                  HttpServletRequest request) {

        String clientIP = getClientIP(request);
        System.out.println("IP клиента: " + clientIP);

        return adminUserService.getPageAllAdminUsers(page, size, search);
    }

    public String getClientIP(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getAdminUser(@PathVariable Long id) {
        AdminUserResponse user = adminUserService.getAdminUserResponseById(id);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Произошла ошибка, пользователь не найден, повторите попытку");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.ok(user);
    }


    @PostMapping({"/create"})
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminUserRequest adminUserRequest) {
        AdminUserResponse createdUser = adminUserService.saveNewUserFromRequest(adminUserRequest);
        return ResponseEntity.ok(createdUser);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable Long id, @Valid @RequestBody AdminUserRequest adminUserRequest) {
        try {
            Optional<AdminUser> existingUser = adminUserService.getAdminUserById(id);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(adminUserService.updateAdminUser(id, adminUserRequest));
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
            boolean deleted = adminUserService.deleteAdminUserById(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
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

    @GetMapping("/profile")
    public ModelAndView getPageProfile(Model model) {
        model.addAttribute("title", "Профиль");
        model.addAttribute("pageActive", "profile");
        return new ModelAndView("user/profile");
    }

    @GetMapping("/getProfile")
    public @ResponseBody ResponseEntity<?> getProfile(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok(adminUserService.getAdminUserProfile(principal.getName()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/saveProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveProfile(
            @RequestPart("adminUserRequest") @Valid AdminUserProfileRequest adminUserRequest,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        if (avatar != null && !avatar.isEmpty()) {
            adminUserRequest.setAvatar(avatar);
        }

        return ResponseEntity.ok(adminUserService.updateAdminUserProfile(adminUserRequest));
    }


}
