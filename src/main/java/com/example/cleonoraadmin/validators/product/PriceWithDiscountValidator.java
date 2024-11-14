package com.example.cleonoraadmin.validators.product;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;



public class PriceWithDiscountValidator
        implements ConstraintValidator<ValidDiscountPrice, Object> {


    private String priceField;
    private String discountPriceField;
    private String message;

    @Override
    public void initialize(ValidDiscountPrice constraintAnnotation) {

        this.priceField = constraintAnnotation.priceField();
        this.discountPriceField = constraintAnnotation.discountPriceField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {

            Field priceField = value.getClass().getDeclaredField(this.priceField);
            Field discountPriceField = value.getClass().getDeclaredField(this.discountPriceField);

            priceField.setAccessible(true);
            discountPriceField.setAccessible(true);

            Double price = (Double) priceField.get(value);
            Double discountPrice = (Double) discountPriceField.get(value);

            if (price == null || discountPrice == null) {
                return true;
            }

            boolean isValid = discountPrice <= price;

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(this.discountPriceField)
                        .addConstraintViolation();
            }

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
