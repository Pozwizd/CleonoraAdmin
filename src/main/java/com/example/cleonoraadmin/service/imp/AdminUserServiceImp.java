package com.example.cleonoraadmin.service.imp;


import com.example.cleonoraadmin.UploadFile;
import com.example.cleonoraadmin.entity.AdminRole;
import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.mapper.AdminUserMapper;
import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserProfileResponse;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.admin.AdminUserResponse;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.service.AdminUserService;
import com.example.cleonoraadmin.specification.AdminUserSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
AdminUserServiceImp
- adminUserRepository: AdminUserRepository
- adminUserMapper: AdminUserMapper
- uploadFile: UploadFile
--
+ findByUsername(username: String): AdminUser
+ saveNewUser(adminUser: AdminUser): AdminUser
+ getPageAllAdminUsers(page: int, size: Integer, search: String): Page<AdminUserResponse>
+ ifUserMoreThan(i: int): boolean
+ getAdminUserById(id: Long): Optional<AdminUser>
+ getAdminUserResponseById(id: Long): AdminUserResponse
+ saveNewUserFromRequest(adminUserRequest: AdminUserRequest): AdminUserResponse
+ updateAdminUser(id: Long, adminUserRequest: AdminUserRequest): AdminUserResponse
+ deleteAdminUserById(id: Long): boolean
+ loadUserByUsername(username: String): UserDetails
+ getAdminUserProfile(username: String): AdminUserProfileResponse
+ updateAdminUserProfile(adminUserRequest: AdminUserProfileRequest): AdminUserProfileResponse
 */
@Service
@AllArgsConstructor
@Slf4j
public class AdminUserServiceImp implements AdminUserService, UserDetailsService {

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;
    private final UploadFile uploadFile;


    @Override
    public AdminUser findByUsername(String username) {
        return adminUserRepository.findByEmail(username)
                .orElse(null);
    }

    @Override
    public AdminUser saveNewUser(AdminUser adminUser) {

        return adminUserRepository.save(adminUser);
    }

    @Override
    public Page<AdminUserResponse> getPageAllAdminUsers(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));

        return adminUserMapper.adminUsertoResponsePage(adminUserRepository.findAll(
                AdminUserSpecification.byActive()
                        .and(AdminUserSpecification.search(search)
                                .and(AdminUserSpecification.getAllExceptRole(AdminRole.ADMIN))),
                pageRequest));
    }


    @Override
    public boolean ifUserMoreThan(int i) {
        return adminUserRepository.count() > i;
    }

    public Optional<AdminUser> getAdminUserById(Long id) {
        return adminUserRepository.findById(id);
    }

    @Override
    public AdminUserResponse getAdminUserResponseById(Long id) {
        return adminUserMapper.adminUsertoResponse(getAdminUserById(id).orElse(null));
    }

    @Override
    public AdminUserResponse saveNewUserFromRequest(AdminUserRequest adminUserRequest) {
        AdminUser adminUser = adminUserMapper.adminUserResponsetoEntity(adminUserRequest);

        return adminUserMapper.adminUsertoResponse(saveNewUser(adminUser));
    }

    @Override
    public AdminUserResponse updateAdminUser(Long id, AdminUserRequest adminUserRequest) {
        AdminUser adminUser = adminUserMapper.adminUserResponsetoEntity(adminUserRequest);
        adminUserRepository.findById(id).ifPresent(user -> {
            adminUser.setAvatar(user.getAvatar());
        });
        return adminUserMapper.adminUsertoResponse(saveNewUser(adminUser));
    }

    @Override
    public boolean deleteAdminUserById(Long id) {
        try {
            adminUserRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public AdminUserProfileResponse getAdminUserProfile(String username) {
        return adminUserMapper.adminUserToProfileResponse(findByUsername(username));
    }



    @Override
    public AdminUserProfileResponse updateAdminUserProfile(AdminUserProfileRequest adminUserRequest) {
        return adminUserMapper.adminUserToProfileResponse(saveNewUser(adminUserMapper.adminUserProfileRequestToEntity(adminUserRequest, uploadFile)));
    }

    @Override
    public Optional<AdminUser> getUserByEmail(String email) {
        return adminUserRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = findByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
