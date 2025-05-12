package com.ihren.processor.validation.contains.in;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
//TODO: create a separate annotation for each Validator. Don't pass any values (String[] allowedItems) in annotation, it must be done in annotation logic
@Constraint(validatedBy = {ContainsInCharSequenceValidator.class, ContainsInStringValidator.class})
public @interface ContainsIn {
    String[] value() default {};
    String message() default "Value '{validated.value}' is not allowed. Allowed values are: {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
