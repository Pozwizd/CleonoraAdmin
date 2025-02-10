package com.example.cleonoraadmin.validators.adminUser.emailValidation;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.validators.EmailUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Optional;


@RequiredArgsConstructor
public class AdminUserEmailUniqueValidator implements ConstraintValidator<EmailUnique, Object> {

    private final AdminUserRepository adminUserRepository;

    private String idField;
    private String emailField;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        this.idField = constraintAnnotation.id();
        this.emailField = constraintAnnotation.email();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        String email = (String) beanWrapper.getPropertyValue(emailField);
        Object id = beanWrapper.getPropertyValue(idField);

        if (email == null || email.isEmpty()) {
            return true; // Email is not provided, consider it valid
        }

        boolean isValid = isValidForAdmin(email, id);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email уже используется")
                    .addPropertyNode(emailField)
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean isValidForAdmin(String email, Object id) {
        Optional<AdminUser> existingUser = adminUserRepository.findByEmail(email);
        return existingUser.isEmpty() || (id != null && existingUser.get().getId().equals(id));
    }
}