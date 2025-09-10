package com.ihren.processor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class ObjectConverter {
    private final ObjectMapper mapper;

    public <T> T read(byte[] bytes, Class<T> clazz) {
        return Try.of(() -> mapper.readValue(bytes, clazz))
                .getOrElseThrow(ex -> new IllegalStateException("Cannot read bytes to " + clazz.getName(), ex));
    }
}
