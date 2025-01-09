package com.example.cleonoraadmin.validators.order;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;



import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CheckTimeValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface CheckTime {
    String startTime() default "startTime";
    String startDate() default "startDate";
    String message() default "Время уже занято";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}