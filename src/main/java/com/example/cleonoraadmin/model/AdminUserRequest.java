package com.example.cleonoraadmin.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class AdminUserRequest {
    @Size(message = "Имя должно быть от 2 до 100 символов", min = 2, max = 100)
    @NotBlank(message = "Имя обязательно")
    private String name;
    private String surname;
    @Pattern(regexp = "^(.+)@(.+)$", message = "Email должен быть в формате example@example.com")
    private String email;
    private String phoneNumber;
    @Size(message = "Пароль должен быть от 6 до 100 символов", min = 6, max = 100)
    @NotBlank(message = "Пароль обязателен")
    private String password;
    private Boolean isActive;
    private String role;
}