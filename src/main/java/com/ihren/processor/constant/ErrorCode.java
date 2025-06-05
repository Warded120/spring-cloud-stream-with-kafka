package com.ihren.processor.constant;

import com.ihren.processor.exception.CacheException;
import com.ihren.processor.exception.MappingException;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.exception.SerializationException;
import jakarta.validation.ValidationException;
import java.util.Arrays;

public enum ErrorCode {
    CACHE_EXCEPTION(CacheException.class),
    MAPPING_EXCEPTION(MappingException.class),
    NOT_FOUND_EXCEPTION(NotFoundException.class),
    SERIALIZATION_EXCEPTION(SerializationException.class),
    VALIDATION_EXCEPTION(ValidationException.class),
    UNKNOWN_EXCEPTION(Exception.class);

    private final Class<? extends Exception> exceptionClass;

    ErrorCode(Class<? extends Exception> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public static ErrorCode from(Exception e) {
        return Arrays.stream(ErrorCode.values())
                .filter(value -> value.exceptionClass.isInstance(e) )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown exception: " + e.getMessage()));
    }
}
