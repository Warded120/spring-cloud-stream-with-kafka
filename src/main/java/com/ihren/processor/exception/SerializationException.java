package com.ihren.processor.exception;

public class SerializationException extends RuntimeException {
    public SerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
