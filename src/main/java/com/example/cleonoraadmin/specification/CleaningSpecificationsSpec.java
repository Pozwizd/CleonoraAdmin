package com.example.cleonoraadmin.specification;

import com.example.cleonoraadmin.entity.CleaningSpecifications;
import org.springframework.data.jpa.domain.Specification;

public interface CleaningSpecificationsSpec {

    static Specification<CleaningSpecifications> search(String searchValue) {

        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.isEmpty()) {
                return null;
            }

            String lowerSearchValue = "%" + searchValue.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), lowerSearchValue),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("unit")), lowerSearchValue)
            );
        };
    }


}
