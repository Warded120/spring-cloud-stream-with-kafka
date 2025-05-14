package com.ihren.processor.validation.discount;

import io.vavr.control.Try;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscountValidator implements ConstraintValidator<Discount, String> {
    private static final Pattern PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,2}$");
    private static final String MAX_DISCOUNT = "100.00";


    @Override
    public boolean isValid(String discount, ConstraintValidatorContext constraintValidatorContext) {
        if (discount == null) {
            return true;
        }
        return Optional.of(discount)
                .map(PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> Try.of(() -> new BigDecimal(discount))
                        .map(n ->
                            n.compareTo(BigDecimal.ZERO) >= 0
                         && n.compareTo(new BigDecimal(MAX_DISCOUNT)) <= 0)
                        .getOrElse(false))
                .orElse(false);
    }
}
