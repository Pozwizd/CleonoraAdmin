package com.example.cleonoraadmin.specification;

import com.example.cleonoraadmin.entity.AdminUser;
import org.springframework.data.jpa.domain.Specification;

public interface AdminUserSpecification {

    static Specification<AdminUser> search(String searchValue) {

        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.isEmpty()) {
                return null;
            }

            String lowerSearchValue = "%" + searchValue.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), lowerSearchValue),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowerSearchValue),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), lowerSearchValue),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")), lowerSearchValue)
            );
        };
    }

    static Specification<AdminUser> byActive() {

        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("isActive"), true);
        };
    }
}
