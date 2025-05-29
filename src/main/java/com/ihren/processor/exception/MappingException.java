package com.ihren.processor.exception;

public class MappingException extends RuntimeException {
    public MappingException(final String message) {
        this(message, null);
    }

    public MappingException(final String message, final Throwable cause) {
        super(message);
        if (cause != null) {
            super.initCause(cause);
        }
    }
}
