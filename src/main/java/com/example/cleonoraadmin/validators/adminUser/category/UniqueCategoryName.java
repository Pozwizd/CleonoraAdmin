package com.example.cleonoraadmin.validators.adminUser.category;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueCategoryNameValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategoryName {
    String id() default "id";
    String name() default "name";
    String message() default "Категория с таким именем уже существует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
