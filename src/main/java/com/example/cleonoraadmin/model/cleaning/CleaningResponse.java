package com.example.cleonoraadmin.model.cleaning;

import com.example.cleonoraadmin.entity.Cleaning;
import lombok.Data;

import java.io.Serializable;

/**
 * Response model for {@link Cleaning}
 */
@Data
public class CleaningResponse implements Serializable {
    Long id;
    String name;
    String description;
    Long categoryId;
    String categoryName;
    Long serviceSpecificationsId;
    String serviceSpecificationsName;
}