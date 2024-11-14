package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface AdminUserService {
    AdminUser findByUsername(String username);

    AdminUser saveNewUser(AdminUser adminUser);

    Page<AdminUserResponse> getPageAllAdminUsers(int page, Integer size, String search);

    boolean ifUserMoreThan(int i);
}

