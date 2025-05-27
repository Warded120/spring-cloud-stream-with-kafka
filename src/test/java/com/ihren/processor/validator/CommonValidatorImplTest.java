package com.ihren.processor.validator;

import jakarta.validation.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@ExtendWith(MockitoExtension.class)
class CommonValidatorImplTest {

    @InjectMocks
    private CommonValidatorImpl commonValidator;

    @Mock
    private Validator validator;

    @Test
    void should_BeOK_when_InputIsValid() {
        //given
        Set<ConstraintViolation<Object>> errors = Set.of();
        Object t = new Object();

        given(validator.validate(t)).willReturn(errors);

        //when
        //then
        assertDoesNotThrow(() -> commonValidator.validate(t));
    }

    @Test
    void should_ThrowValidationException_when_InputIsInvalid() {
        //given
        ConstraintViolation violation = mock();
        Set<ConstraintViolation<Object>> errors = Set.of(violation);
        Object t = new Object();

        given(validator.validate(t)).willReturn(errors);

        //when
        //then
        assertThrows(ValidationException.class, () -> commonValidator.validate(t));
    }
}