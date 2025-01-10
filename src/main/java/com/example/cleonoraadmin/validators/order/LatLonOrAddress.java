package com.example.cleanorarest.validators.order;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LatLonOrAddressValidator.class)
public @interface LatLonOrAddress {
    String message() default "Необходимо указать координаты (широта, долгота) или адрес.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}