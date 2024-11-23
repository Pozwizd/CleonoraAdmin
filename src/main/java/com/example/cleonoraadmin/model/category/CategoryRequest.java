package com.example.cleonoraadmin.model.category;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for {@link com.example.cleonoraadmin.entity.Category}
 */
@Data
public class CategoryRequest {
    private Long id;
    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 2, max = 100)
    private String description;
}
