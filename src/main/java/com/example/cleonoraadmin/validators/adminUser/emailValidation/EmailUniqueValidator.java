package com.example.cleonoraadmin.validators.adminUser.emailValidation;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.repository.CustomerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * 
 */
@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, Object> {

    private final CustomerRepository customerRepository;
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

        // Используем BeanWrapper для извлечения полей
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        String email = (String) beanWrapper.getPropertyValue(emailField);
        Object id = beanWrapper.getPropertyValue(idField);

        if (email == null || email.isEmpty()) {
            return true;  // Если email пустой, то валидируем как валидное значение
        }

        // Определяем, для какого типа объекта мы делаем проверку
        boolean isValid;
        if (value instanceof CustomerRequest) {
            isValid = isValidForCustomer(email, id);
        } else if (value instanceof AdminUserRequest) {
            isValid = isValidForAdmin(email, id);
        } else if (value instanceof AdminUserProfileRequest) {
            isValid = isValidForAdmin(email, id);
        } else {
            // Если тип не найден, можно выбросить исключение или вернуть false
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Неизвестный тип объекта для валидации")
                    .addPropertyNode(emailField)
                    .addConstraintViolation();
            return false;
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email уже используется")
                    .addPropertyNode(emailField)
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean isValidForCustomer(String email, Object id) {
        // Используем репозиторий клиента для проверки
        return customerRepository.findByEmail(email)
                .map(existingCustomer -> id != null && id.equals(existingCustomer.getId()))
                .orElse(true);
    }

    private boolean isValidForAdmin(String email, Object id) {
        // Используем репозиторий администратора для проверки
        return adminUserRepository.findByEmail(email)
                .map(existingAdmin -> id != null && id.equals(existingAdmin.getId()))
                .orElse(true);
    }
}
