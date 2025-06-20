package com.ihren.processor.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    @UtilityClass
    public static final class Kafka {
        @UtilityClass
        public static final class Headers {
            public static final String ORIGINAL_TOPIC = "x-original-topic";
            public static final String IS_DLT = "x-is-dlt";
            public static final String EXCEPTION_MESSAGE = "x-exception-message";
            public static final String ERROR_CODE = "x-error-code";
        }
    }
}