package com.ihren.processor.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, T data) {
        return Try.of(() -> objectMapper.writeValueAsBytes(data))
                .getOrElseThrow(ex -> new SerializationException("failed to serialize T", ErrorCode.SERIALIZATION_EXCEPTION));
    }
}
