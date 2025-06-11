package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class SerializationException extends ApplicationException {
    public SerializationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public SerializationException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
