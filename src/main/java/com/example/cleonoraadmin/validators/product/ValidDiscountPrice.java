package com.example.cleonoraadmin.validators.product;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Constraint(validatedBy = PriceWithDiscountValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDiscountPrice {
    String message() default "Цена со скидкой не может быть больше обычной цены";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String priceField();

    String discountPriceField();
}
