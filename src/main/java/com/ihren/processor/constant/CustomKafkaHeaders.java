package com.ihren.processor.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomKafkaHeaders {
    public static final String IS_DLT = "is-dlt";
    public static final String EXCEPTION_MESSAGE = "exception-message";
    public static final String ERROR_CODE = "error-code";
}
