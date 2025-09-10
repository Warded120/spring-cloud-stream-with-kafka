package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class MappingException extends ApplicationException {
    public MappingException(String message) {
        super(message, ErrorCode.MAPPING);
    }
}