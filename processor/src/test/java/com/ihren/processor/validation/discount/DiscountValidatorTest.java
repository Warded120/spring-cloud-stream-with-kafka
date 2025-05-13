package com.ihren.processor.validation.discount;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscountValidatorTest {
    private final DiscountValidator validator = new DiscountValidator();

    @Test
    void should_ReturnTrue_when_InputIsValid() {
        //given
        String value = "99.99";

        //when
        //then
        assertTrue(validator.isValid(value, null));
    }

    @Test
    void should_ReturnTrue_when_InputIsNull() {
        //when
        //then
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void should_ReturnFalse_when_InputIsInvalid() {
        //given
        String value = "invalid";

        //when
        //then
        assertFalse(validator.isValid(value, null));
    }
}