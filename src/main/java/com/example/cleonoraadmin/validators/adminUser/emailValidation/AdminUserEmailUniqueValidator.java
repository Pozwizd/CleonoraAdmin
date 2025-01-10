package com.example.cleonoraadmin.validators.adminUser.emailValidation;

import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.validators.EmailUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;


@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, Object> {

    private final AdminUserRepository adminUserRepository;

    private String emailField;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        String idField = constraintAnnotation.id();
        this.emailField = constraintAnnotation.email();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof AdminUserProfileRequest request)) {
            return true;
        }

        String email = request.getEmail();
        Long id = request.getId();

        if (email == null || email.isEmpty()) {
            return true; // Email is not provided, consider it valid
        }

        // If id is null, it's a new user; check if email already exists
        // If id is present, check if email exists for a different user
        boolean emailExists = adminUserRepository.findByEmail(email)
                .map(existingUser -> id == null || !id.equals(existingUser.getId()))
                .orElse(false);

        if (emailExists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email уже используется")
                    .addPropertyNode(emailField)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}