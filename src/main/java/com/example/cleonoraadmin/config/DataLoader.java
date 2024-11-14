package com.example.cleonoraadmin.config;

import com.example.cleonoraadmin.entity.AdminRole;
import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;


@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final Faker faker;
    private final AdminUserService adminUserService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadUsers() {
        if (adminUserService.findByUsername("admin@gmail.com") == null){
            AdminUser adminUser = new AdminUser();
            adminUser.setName("Admin");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPhoneNumber(faker.phoneNumber().cellPhone());
            adminUser.setPassword("admin");
            adminUserService.saveNewUser(adminUser);
        }

        while (!adminUserService.ifUserMoreThan(5)){
            AdminUser adminUser = new AdminUser();
            adminUser.setName(faker.name().fullName());
            adminUser.setSurname(faker.name().lastName());
            adminUser.setEmail(faker.internet().emailAddress());
            adminUser.setPhoneNumber(faker.phoneNumber().cellPhone());
            adminUser.setPassword(faker.internet().password());
            adminUser.setRole(AdminRole.MANAGER);
            adminUserService.saveNewUser(adminUser);

        }

    }


}