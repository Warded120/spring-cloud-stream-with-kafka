package com.ihren.processor.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        this(message, null);
    }

    public NotFoundException(final String message, final Throwable cause) {
        super(message);
        if (cause != null) {
            super.initCause(cause);
        }
    }
}
