package com.ihren.processor.util;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTimeUtilsTest {
    @Test
    void parseInstantShouldReturnCorrectInstantWhenValidInput() {
        // given
        String input = "2024-05-10T15:30:00Z";

        // when
        Instant actual = DateTimeUtils.parseInstant(input);

        // then
        assertEquals(Instant.parse("2024-05-10T15:30:00Z"), actual);
    }

    @Test
    void parseInstantShouldThrowExceptionWhenInputIsInvalid() {
        // given
        String input = "invalid-date-time";

        // when / then
        assertThrows(DateTimeParseException.class, () -> DateTimeUtils.parseInstant(input));
    }
}