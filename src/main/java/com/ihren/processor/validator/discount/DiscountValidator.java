package com.ihren.processor.validator.discount;

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
        return Optional.ofNullable(discount)
                .flatMap(ifNotNull -> Optional.of(discount)
                        .map(PATTERN::matcher)
                        .filter(Matcher::matches)
                        .flatMap(ifMatches ->
                                Try.of(() -> new BigDecimal(discount))
                                        .map(number ->
                                                number.compareTo(BigDecimal.ZERO) >= 0
                                                        && number.compareTo(new BigDecimal(MAX_DISCOUNT)) <= 0
                                        )
                                        .toJavaOptional()
                        )
                        .or(() -> Optional.of(false))
                )
                .orElse(true);

    }
}
