package com.example.cleonoraadmin.validators.customer.emailValidation;


import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.validators.EmailUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomerEmailUniqueValidator implements ConstraintValidator<EmailUnique, Object> {

    private final CustomerRepository customerRepository;

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
            return true;
        }

        boolean isValid = isValidForCustomer(email, id);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email уже используется")
                    .addPropertyNode(emailField)
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean isValidForCustomer(String email, Object id) {
        Optional<Customer> existingCustomer = customerRepository.getCustomerByEmail(email);
        return existingCustomer.isEmpty() || (id != null && existingCustomer.get().getId().equals(id));
    }
}