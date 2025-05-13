package com.ihren.processor.validation.currency;

import com.ihren.processor.constant.Currency;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ValidCurrencyValidatorTest {
    private final ValidCurrencyValidator validator = new ValidCurrencyValidator();

    @Test
    void should_ReturnTrue_when_InputIsValid() {
        //given
        String currencyName = Currency.USD.name();
        ReflectionTestUtils.setField(validator, "allowedValues", Set.of(currencyName));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //when
        //then
        assertTrue(validator.isValid(currencyName, context));
    }

    @Test
    void should_ReturnFalse_when_InputIsInvalid() {
        //given
        String value = Currency.USD.name();
        Set<String> allowedValues = Set.of("EUR");
        String message = "Value '" + value + "' is not allowed. Allowed values are: " + String.join(", ", allowedValues);

        ReflectionTestUtils.setField(validator, "allowedValues", allowedValues);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock();

        given(context.buildConstraintViolationWithTemplate(message)).willReturn(builder);

        //when
        assertFalse(validator.isValid(value, context));

        //then
        then(context).should().disableDefaultConstraintViolation();
        then(context).should().buildConstraintViolationWithTemplate(message);
        then(builder).should().addConstraintViolation();
    }
}