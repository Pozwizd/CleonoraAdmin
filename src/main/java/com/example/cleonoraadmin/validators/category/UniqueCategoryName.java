package com.example.cleonoraadmin.validators.category;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

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
