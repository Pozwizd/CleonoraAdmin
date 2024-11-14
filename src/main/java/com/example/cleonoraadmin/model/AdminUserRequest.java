package com.example.cleonoraadmin.model;

import lombok.Data;



@Data
public class AdminUserRequest {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password; // Только при создании или смене пароля
    private Boolean isActive;
    private String role; // String, чтобы передавать значение в формате Enum
}