package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.mapper.AdminUserMapper;
import com.example.cleonoraadmin.model.AdminUserResponse;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.cleonoraadmin.specification.AdminUserSpecification;

@Service
@AllArgsConstructor
@Slf4j
public class AdminUserServiceImp implements AdminUserService, UserDetailsService {

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;


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
        PageRequest pageRequest = PageRequest.of(page, size);

        return adminUserMapper.adminUsertoResponsePage(adminUserRepository.findAll(
                AdminUserSpecification.byActive()
                        .and(AdminUserSpecification.search(search)),
                pageRequest));
    }

    @Override
    public boolean ifUserMoreThan(int i) {
        return adminUserRepository.count() > i;
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
