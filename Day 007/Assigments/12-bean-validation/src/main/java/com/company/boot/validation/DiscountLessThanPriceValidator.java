package com.company.boot.validation;

import com.company.boot.dto.ProductRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** The logic behind @DiscountLessThanPrice. */
public class DiscountLessThanPriceValidator
        implements ConstraintValidator<DiscountLessThanPrice, ProductRequest> {

    @Override
    public boolean isValid(ProductRequest request, ConstraintValidatorContext context) {
        if (request.getPrice() == null || request.getDiscountPrice() == null) {
            return true;    // nothing to compare; missing price is reported by @NotNull
        }
        if (request.getDiscountPrice().compareTo(request.getPrice()) < 0) {
            return true;
        }
        // Attach the error to the discountPrice field instead of the whole object
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
               .addPropertyNode("discountPrice")
               .addConstraintViolation();
        return false;
    }
}
