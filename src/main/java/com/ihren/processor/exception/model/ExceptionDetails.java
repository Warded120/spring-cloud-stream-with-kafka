package com.ihren.processor.exception.model;

import com.ihren.processor.constant.ErrorCode;

public record ExceptionDetails (
        ErrorCode errorCode,
        String message
) { }
