package com.ihren.processor.constant;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {
    @Test
    void should_ReturnAccount_when_IdIsValid() {
        //given
        String expected = Account.MAIN.getName();
        String id = "1";

        //when
        Optional<String> actual = Account.getNameById(id);

        //then
        assertEquals(expected, actual.orElse(null));
    }

    @Test
    void should_ThrowIllegalArgumentException_when_IdIsInvalid() {
        //given
        String id = "invalid";

        //when
        //then
        assertEquals(Optional.empty(), Account.getNameById(id));
    }
}