package com.example.cleonoraadmin.validators.customer.emailValidation;


import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.repository.CustomerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, CustomerRequest> {

    private final CustomerRepository customerRepository;


    @Override
    public void initialize(EmailUnique constraintAnnotation) {
    }

    @Override
    public boolean isValid(CustomerRequest customerRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (customerRequest == null || customerRequest.getEmail() == null || customerRequest.getEmail().isEmpty()) {
            return true;
        }

        Optional<Customer> existingUser = customerRepository.getCustomerByEmail(customerRequest.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(customerRequest.getId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Email уже используется")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }


}
