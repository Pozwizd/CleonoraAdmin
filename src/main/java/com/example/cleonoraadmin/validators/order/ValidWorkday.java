package com.example.cleonoraadmin.validators.order;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = WorkdayValidator.class)
@Target({ElementType.TYPE, FIELD})
@Retention(RUNTIME)
public @interface ValidWorkday {
    String startTime() default "startTime";
    String startDate() default "startDate";
    String message() default "Выбранная дата является выходным днем";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}