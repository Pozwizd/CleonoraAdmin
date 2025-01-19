package com.example.cleonoraadmin.specification;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;


public interface OrderSpecification {

    static Specification<Order> searchByCustomerField(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + search.toLowerCase() + "%";
            Join<Order, Customer> customerJoin = root.join("customer");

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("surname")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("email")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("phoneNumber")), likePattern)
            );
        };
    }

    static Specification<Order> byStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    static Specification<Order> orderUpdateLastDaily() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDateTime startDate = startOfWeek.atStartOfDay();
        LocalDateTime endDate = endOfWeek.atTime(23, 59, 59);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("updated"), startDate, endDate);
    }

    static Specification<Order> updatedWithinLastWeek() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("updated"), LocalDateTime.now().minusWeeks(1));
    }


}