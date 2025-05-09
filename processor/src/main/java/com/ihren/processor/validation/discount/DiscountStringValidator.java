package com.ihren.processor.validation.discount;

import io.vavr.control.Try;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class DiscountStringValidator implements ConstraintValidator<ValidDiscount, String> {
    private static final Pattern PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,2}$");


    @Override
    public boolean isValid(String discount, ConstraintValidatorContext constraintValidatorContext) {
        if (discount == null) {
            return true;
        }

        if (!PATTERN.matcher(discount).matches()) {
            return false;
        }

        return Try.of(() -> {
            BigDecimal number = new BigDecimal(discount);
            return number.compareTo(BigDecimal.ZERO) >= 0
                    && number.compareTo(new BigDecimal("100.00")) <= 0;
        })
        .recover(ex -> false)
        .get();
    }
}
