package com.example.cleonoraadmin.model.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class AdminUserProfileRequest {
    private Long id;

    @Size(message = "Имя должно быть от 2 до 100 символов", min = 2, max = 100)
    @NotBlank(message = "Имя обязательно")
    private String name;
    private String surname;
    private MultipartFile avatar;
    @Pattern(regexp = "^(.+)@(.+)$", message = "Email должен быть в формате example@example.com")
    private String email;
    private String phoneNumber;
    @Size(message = "Пароль должен быть от 6 до 100 символов", min = 6, max = 100)
    @NotBlank(message = "Пароль обязателен")
    private String password;
    private Boolean isActive;
    private String role;

}