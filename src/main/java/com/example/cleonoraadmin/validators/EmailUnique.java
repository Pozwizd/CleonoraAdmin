package com.example.cleonoraadmin.validators;

import com.example.cleonoraadmin.validators.adminUser.emailValidation.AdminUserEmailUniqueValidator;
import com.example.cleonoraadmin.validators.customer.emailValidation.CustomerEmailUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = {AdminUserEmailUniqueValidator.class, CustomerEmailUniqueValidator.class})
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {
    String id() default "id";
    String email() default "email";
    String message() default "Email уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
