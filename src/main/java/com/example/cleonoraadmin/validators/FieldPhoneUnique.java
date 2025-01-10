package com.example.cleonoraadmin.validators.adminUser.phoneNumberValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = )
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPhoneUnique {
    String id() default "id";
    String phoneNumber() default "phoneNumber";
    String message() default "Номер телефона уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
