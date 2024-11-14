package com.example.cleonoraadmin.validators.customer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class PastOrPresentDateValidator implements ConstraintValidator<PastOrPresentDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return date.isBefore(LocalDate.now());
    }
}
