package com.ihren.processor.validation.contains.in;

import com.ihren.processor.constant.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainsInCurrencyValidator implements ConstraintValidator<ContainsIn, String> {

    private static final Set<String> allowedValues = Stream.of(Currency.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(allowedValues.contains(value)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(
                        "Value '" + value + "' is not allowed. Allowed values are: " + String.join(", ", allowedValues)
                )
                .addConstraintViolation();

        return false;
    }
}
