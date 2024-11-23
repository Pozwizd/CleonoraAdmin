package com.example.cleonoraadmin.model.admin;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String role;
    private List<Long> orderIds;
}
