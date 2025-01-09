package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.model.SalesChartProjection;
import com.example.cleonoraadmin.model.TopCleaningProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT oc.cleaning.name AS name, COUNT(o.id) AS count " +
            "FROM Order o JOIN o.orderCleanings oc " +
            "WHERE o.startDate >= :startDate " +
            "GROUP BY oc.cleaning.name " +
            "ORDER BY COUNT(o.id) DESC")
    List<TopCleaningProjection> findTopCleanings(@Param("startDate") LocalDate startDate, Pageable pageable);



    @Query("SELECT YEAR(o.startDate) as year, MONTH(o.startDate) as month, c.name as name, COUNT(o.id) as count " +
            "FROM Order o JOIN o.orderCleanings oc JOIN oc.cleaning c " +
            "WHERE o.startDate >= :startDate " +
            "GROUP BY YEAR(o.startDate), MONTH(o.startDate), c.name")
    List<SalesChartProjection> findSalesDataByMonth(@Param("startDate") LocalDate startDate);


    @Query("SELECT COUNT(o.id) FROM Order o WHERE o.status = 'COMPLETED'")
    Integer countCompletedOrders();

}


