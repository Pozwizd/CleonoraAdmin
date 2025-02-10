package com.example.cleonoraadmin.validators.adminUser.phoneNumberValidation;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.validators.EmailUnique;
import com.example.cleonoraadmin.validators.FieldPhoneUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Optional;


@RequiredArgsConstructor
public class AdminUserPhoneNumberUniqueValidator implements ConstraintValidator<FieldPhoneUnique, Object> {

    private final AdminUserRepository adminUserRepository;

    private String idField;
    private String phoneNumberField;

    @Override
    public void initialize(FieldPhoneUnique constraintAnnotation) {
        this.idField = constraintAnnotation.id();
        this.phoneNumberField = constraintAnnotation.phoneNumber();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        String phoneNumber = (String) beanWrapper.getPropertyValue(phoneNumberField);
        Object id = beanWrapper.getPropertyValue(idField);

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }

        boolean isValid = isValidForAdminUser(phoneNumber, id);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(phoneNumberField)
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean isValidForAdminUser(String phoneNumber, Object id) {
        Optional<AdminUser> existingAdminUser = adminUserRepository.findByPhoneNumber(phoneNumber);
        return existingAdminUser.isEmpty() || (id != null && existingAdminUser.get().getId().equals(id));
    }
}