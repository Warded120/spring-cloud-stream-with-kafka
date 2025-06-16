package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION);
    }
}
