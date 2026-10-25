package com.company.boot.validation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** The logic behind @ValidSku. */
public class SkuValidator implements ConstraintValidator<ValidSku, String> {

    private static final Pattern SKU_FORMAT = Pattern.compile("[A-Z]{3}-\\d{5}");

    private List<String> allowedPrefixes;

    @Override
    public void initialize(ValidSku annotation) {
        this.allowedPrefixes = Arrays.asList(annotation.prefixes());
    }

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        if (sku == null || sku.isBlank()) {
            return true;    // "required" is @NotBlank's job - avoids a second error for the same field
        }
        return SKU_FORMAT.matcher(sku).matches()
                && allowedPrefixes.contains(sku.substring(0, 3));
    }
}
