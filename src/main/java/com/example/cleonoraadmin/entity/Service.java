package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;


/**
 Service
 - id: Long
 - name: String
 - description: String
 - duration: Duration
 - category: Category
 - cleaningService: CleaningService
 --
 - getId(): Long
 - setId(Long id)
 - getName(): String
 - setName(String name)
 - getDescription(): String
 - setDescription(String description)
 - getDuration(): Duration
 - setDuration(Duration duration)
 - getCategory(): Category
 - setCategory(Category category)
 - getCleaningService(): CleaningService
 - setCleaningService(CleaningService cleaningService)
 */




@Entity
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Duration duration;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "cleaning_service_id", nullable = false)
    private CleaningService cleaningService;
}
