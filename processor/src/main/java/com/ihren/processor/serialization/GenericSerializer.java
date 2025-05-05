package com.ihren.processor.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import java.util.Map;

public class GenericSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper;

    public GenericSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, T data) {
        return Try.of(() -> objectMapper.writeValueAsBytes(data))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
