package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class SerializationException extends ApplicationException {
    public SerializationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.SERIALIZATION_EXCEPTION);
    }
}
