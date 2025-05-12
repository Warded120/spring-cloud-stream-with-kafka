package com.ihren.processor.validation.contains.in;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.Set;

public class ContainsInCharSequenceValidator implements ConstraintValidator<ContainsIn, CharSequence> {

    private Set<String> allowedValues;

    @Override
    public void initialize(ContainsIn annotation) {
        this.allowedValues = Set.of(annotation.value());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return Optional.ofNullable(value)
                .map(CharSequence::toString)
                .map(allowedValues::contains)
                .filter(contains -> contains)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    "Value '" + value + "' is not allowed. Allowed values are: " + String.join(", ", allowedValues)
                            )
                            .addConstraintViolation();
                    return false;
                });
    }
}
