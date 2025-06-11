package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class CacheException extends ApplicationException {
    public CacheException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
