package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserProfileResponse;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.admin.AdminUserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface AdminUserService {
    AdminUser findByUsername(String username);

    AdminUser saveNewUser(AdminUser adminUser);

    Page<AdminUserResponse> getPageAllAdminUsers(int page, Integer size, String search);

    boolean ifUserMoreThan(int i);

    Optional<AdminUser> getAdminUserById(Long id);

    AdminUserResponse getAdminUserResponseById(Long id);

    AdminUserResponse saveNewUserFromRequest(@Valid AdminUserRequest adminUserRequest);

    AdminUserResponse updateAdminUser(Long id, @Valid AdminUserRequest adminUserRequest);

    boolean deleteAdminUserById(Long id);

    AdminUserProfileResponse getAdminUserProfile(String username);

    AdminUserProfileResponse updateAdminUserProfile(@Valid AdminUserProfileRequest adminUserRequest);

    Optional<AdminUser> getUserByEmail(String email);
}

