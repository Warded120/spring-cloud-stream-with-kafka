
package com.ihren.processor.validation.discount;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(validatedBy = DiscountStringValidator.class)
public @interface ValidDiscount {
    String message() default "discount must be up to 100.00";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
