package com.company.boot.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * OPTIONAL EXTENSION - custom FIELD-level constraint.
 *
 * A SKU looks like "ELE-10234": a known 3-letter department prefix, a dash
 * and 5 digits. The prefix list is an annotation attribute, so it can differ
 * per use: @ValidSku(prefixes = {"BOK"}).
 *
 * The three members message/groups/payload are required by the
 * Bean Validation specification for every constraint annotation.
 */
@Documented
@Constraint(validatedBy = SkuValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSku {

    String message() default "sku must look like XXX-99999 with prefix one of {prefixes}";

    String[] prefixes() default {"ELE", "BOK", "GRO"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
