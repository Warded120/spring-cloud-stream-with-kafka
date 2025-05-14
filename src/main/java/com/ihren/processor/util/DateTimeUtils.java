package com.ihren.processor.util;

import lombok.experimental.UtilityClass;
import java.time.Instant;

@UtilityClass
public class DateTimeUtils {
    public static Instant parseInstant(String dateTime) {
        return Instant.parse(dateTime);
    }
}
