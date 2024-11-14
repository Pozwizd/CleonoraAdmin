package com.example.cleonoraadmin.validators.customer;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PastOrPresentDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PastOrPresentDate {
    String message() default "Дата рождения не может быть в будущем";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
