package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
