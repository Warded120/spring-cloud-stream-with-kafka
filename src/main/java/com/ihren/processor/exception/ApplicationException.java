package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.model.ExceptionDetails;

public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;
    public ApplicationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ExceptionDetails getExceptionDetails() {
        return new ExceptionDetails(errorCode, super.getMessage());
    }
}
