package com.ihren.processor.exception;

import com.ihren.processor.constant.ErrorCode;

public class CacheException extends ApplicationException {
    public CacheException(String message) {
        super(message, ErrorCode.CACHE_EXCEPTION);
    }
}
