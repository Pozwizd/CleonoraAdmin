package com.example.cleonoraadmin.specification;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CustomerSpecification {

    static Specification<Customer> search(String searchValue){
        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.isEmpty()) {
                return null;
            }

            String lowerSearchValue = "%" + searchValue.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), lowerSearchValue),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("")), lowerSearchValue)
            );
        };
    }

    static Specification<Customer> registrationLastDaily() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDateTime startDate = startOfWeek.atStartOfDay();
        LocalDateTime endDate = endOfWeek.atTime(23, 59, 59);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("registrationDate"), startDate, endDate);
    }

    static Specification<Customer> byDeleted(boolean deleted) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), deleted);
    }

    static Specification<Customer> byEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }


    static Specification<Customer> lastOrderWithinDays(int days) {
        return (root, query, criteriaBuilder) -> {
            Join<Customer, Order> orders = root.join("orders", JoinType.LEFT);
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(orders.get("updated"), LocalDateTime.now().minusDays(days)),
                    criteriaBuilder.equal(orders.get("status"), OrderStatus.COMPLETED)
            );
        };
    }
    static Specification<Customer> lastOrderWithinDaysAndWeekAgo(int days) {
        return (root, query, criteriaBuilder) -> {
            Join<Customer, Order> orders = root.join("orders", JoinType.LEFT);
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(orders.get("updated"), LocalDateTime.now().minusDays(days).minusWeeks(1)),
                    criteriaBuilder.equal(orders.get("status"), OrderStatus.COMPLETED)
            );
        };
    }
}
