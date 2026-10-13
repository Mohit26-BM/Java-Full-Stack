package com.company.boot.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * OPTIONAL EXTENSION - custom CLASS-level (cross-field) constraint.
 * discountPrice, when given, must be lower than price.
 * Built-in annotations only see one field, so this needs a custom validator.
 */
@Documented
@Constraint(validatedBy = DiscountLessThanPriceValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscountLessThanPrice {

    String message() default "discountPrice must be less than price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
