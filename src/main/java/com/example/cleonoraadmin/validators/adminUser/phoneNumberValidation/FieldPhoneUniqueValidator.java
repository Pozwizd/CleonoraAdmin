package com.example.cleonoraadmin.validators.adminUser.phoneNumberValidation;

import com.example.cleonoraadmin.repository.AdminUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

@RequiredArgsConstructor
public class FieldPhoneUniqueValidator implements ConstraintValidator<FieldPhoneUnique, Object> {
    private final AdminUserRepository userRepository;
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

        var existingUserOptional = userRepository.findByPhoneNumber(phoneNumber);

        if (existingUserOptional.isEmpty()) {
            return true;
        }

        var existingUser = existingUserOptional.get();
        boolean isValid = id != null && existingUser.getId().equals(id);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Номер телефона уже используется")
                    .addPropertyNode(phoneNumberField)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
