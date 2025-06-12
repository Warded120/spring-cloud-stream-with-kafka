package com.ihren.processor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import io.vavr.control.Try;

public class MapperUtils {
    private static final ObjectMapper mapper = getObjectMapper();

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapperConfig.configure(mapper);
        return mapper;
    }

    public static <T> T read(byte[] bytes, Class<T> clazz) {
        return Try.of(() -> mapper.readValue(bytes, clazz))
                .getOrElseThrow(ex -> new IllegalStateException("Cannot read bytes to " + clazz.getName(), ex));
    }
}
