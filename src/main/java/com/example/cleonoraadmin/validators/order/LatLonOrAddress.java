package com.example.cleonoraadmin.validators.order;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LatLonOrAddressValidator.class)
public @interface LatLonOrAddress {
    String message() default "Необходимо указать координаты (широта, долгота) или адрес.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}