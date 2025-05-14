package com.ihren.processor.validation.currency;

import com.ihren.processor.constant.CurrencyCode;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class CurrencyValidatorTest {
    private final CurrencyValidator validator = new CurrencyValidator();

    @Test
    void should_InitializeAllowedItems() {
        //given
        Set<String> expected = Stream.of(CurrencyCode.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Currency annotation = mock();

        //when
        validator.initialize(annotation);

        //then
        assertEquals(expected, ReflectionTestUtils.getField(validator, "allowedValues"));
    }

    @Test
    void should_ReturnTrue_when_InputIsValid() {
        //given
        String currencyName = CurrencyCode.USD.name();
        ReflectionTestUtils.setField(validator, "allowedValues", Set.of(currencyName));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //when
        //then
        assertTrue(validator.isValid(currencyName, context));
    }

    @Test
    void should_ReturnFalse_when_InputIsInvalid() {
        //given
        String value = CurrencyCode.USD.name();
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