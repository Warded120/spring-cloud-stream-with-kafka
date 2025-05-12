package com.ihren.processor.validation.contains.in;

import com.ihren.processor.constant.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainsInStringValidator implements ConstraintValidator<ContainsIn, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(ContainsIn annotation) {
        this.allowedValues = annotation.value().length > 0
                ? Set.of(annotation.value())
                : Stream.of(Currency.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Optional.ofNullable(value)
                .map(String::toString)
                .map(allowedValues::contains)
                .filter(contains -> contains)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context
                            .buildConstraintViolationWithTemplate(
                                    "Value '" + value + "' is not allowed. Allowed values are: " + String.join(", ", allowedValues)
                            )
                            .addConstraintViolation();

                    return false;
                });
    }
}
