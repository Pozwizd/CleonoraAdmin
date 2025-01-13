package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;


/**
 Cleaning
- id: Long
- name: String
- description: String
- category: Category
- cleaningSpecifications: CleaningSpecifications
--
+ getId(): Long
+ setId(Long id)
+ getName(): String
+ setName(String name)
+ getDescription(): String
+ setDescription(String description)
+ getCategory(): Category
+ setCategory(Category category)
+ getCleaningSpecifications(): CleaningSpecifications
+ setCleaningSpecifications(CleaningSpecifications cleaningSpecifications)
 */




@Entity
@Data
public class Cleaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "cleaning_specifications_id", nullable = false)
    private CleaningSpecifications cleaningSpecifications;
}
