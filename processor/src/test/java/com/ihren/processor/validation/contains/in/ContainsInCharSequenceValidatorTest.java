package com.ihren.processor.validation.contains.in;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ContainsInCharSequenceValidatorTest {
    private final ContainsInCharSequenceValidator validator = new ContainsInCharSequenceValidator();

    @Test
    void should_initialize() {
        //given
        Set<String> expected = Set.of("1");

        ContainsIn annotation = mock();
        given(annotation.value()).willReturn(new String[] {"1"});

        //when
        validator.initialize(annotation);

        //then
        assertEquals(expected, ReflectionTestUtils.getField(validator, "allowedValues"));
    }

    @Test
    void should_ReturnTrue_when_InputIsValid() {
        //given
        CharSequence sequence = "1";
        ReflectionTestUtils.setField(validator, "allowedValues", Set.of(sequence));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //when
        //then
        assertTrue(validator.isValid(sequence, context));
    }

    @Test
    void should_ReturnFalse_when_InputIsInvalid() {
        //given
        CharSequence value = "0";
        Set<String> allowedValues = Set.of("1");
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