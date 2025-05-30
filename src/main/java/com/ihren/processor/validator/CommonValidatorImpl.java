package com.ihren.processor.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommonValidatorImpl<T> implements CommonValidator<T> {
    private final Validator validator;

    public T validate(T t) {
        Set<ConstraintViolation<T>> errors = validator.validate(t);

        if (!errors.isEmpty()) {
            throw new ValidationException("Errors were found: "
                    + String.join(
                            ", ",
                            errors.stream()
                                .map(ConstraintViolation::getMessage)
                                .toArray(String[]::new)
                    )
            );
        }
        return t;
    }
}
