package com.ihren.processor.validator.account.id;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AccountIdValidator.class)
public @interface AccountId {
    String message() default "Value '{validated.value}' is not allowed. Allowed values are: {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
