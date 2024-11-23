package com.example.cleonoraadmin.model.category;

import lombok.Data;

/**
 * DTO for {@link com.example.cleonoraadmin.entity.Category}
 */
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}
