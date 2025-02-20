package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.OrderCleaning;
import com.example.cleonoraadmin.entity.TimeSlot;
import com.example.cleonoraadmin.entity.Workday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long>, JpaSpecificationExecutor<TimeSlot> {
    List<TimeSlot> findByDate(LocalDate date);

    List<TimeSlot> findByWorkdayOrderByStartTime(Workday workday);

    List<TimeSlot> findAllByDate(LocalDate startDate);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.date BETWEEN :startDate AND :endDate " +
            "AND ts.endTime > :startTime AND ts.startTime < :endTime")
    List<TimeSlot> findConflictingTimeSlots(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("startTime") LocalTime startTime,
                                            @Param("endTime") LocalTime endTime);

    List<TimeSlot> findByOrderCleaning(OrderCleaning orderCleaning);

    void deleteAllByOrderCleaning(OrderCleaning orderCleaning);

    void deleteByOrderCleaning(OrderCleaning oc);

    Optional<TimeSlot> findFirstByDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime startTime, LocalTime endTime);

    TimeSlot findByDateAndStartTimeAndEndTime(LocalDate date, LocalTime startTime, LocalTime endTime);
}