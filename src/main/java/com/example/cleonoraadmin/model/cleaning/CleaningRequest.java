package com.example.cleonoraadmin.model.cleaning;

import com.example.cleonoraadmin.entity.Cleaning;
import lombok.Data;

import java.io.Serializable;

/**
 * Request model for {@link Cleaning}
 */
@Data
public class CleaningRequest implements Serializable {
    Long id;
    String name;
    String description;
    Long categoryId;
    Long serviceSpecificationsId;
}