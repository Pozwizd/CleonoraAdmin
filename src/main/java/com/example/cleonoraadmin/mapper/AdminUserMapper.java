package com.example.cleonoraadmin.mapper;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.AdminUserRequest;
import com.example.cleonoraadmin.model.AdminUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;

import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminUserMapper {

    // Преобразование из DTO в сущность
    @Mapping(source = "name", target = "name")
    AdminUser adminUserResponsetoEntity(AdminUserRequest adminUserRequest);

    // Преобразование из сущности в DTO
    @Mapping(source = "name", target = "name")
    AdminUserResponse adminUsertoResponse(AdminUser adminUser);

    // Преобразование списка DTO в список сущностей
    List<AdminUser> toEntityList(List<AdminUserRequest> adminUserRequests);

    // Преобразование списка сущностей в список DTO
    List<AdminUserResponse> toResponseList(List<AdminUser> adminUsers);

    // Преобразование страницы сущностей в страницу DTO
    default Page<AdminUserResponse> adminUsertoResponsePage(Page<AdminUser> adminUserPage) {
        return adminUserPage.map(this::adminUsertoResponse);
    }
}
