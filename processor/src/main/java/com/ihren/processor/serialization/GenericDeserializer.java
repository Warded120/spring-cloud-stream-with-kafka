package com.ihren.processor.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

public class GenericDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> targetClass;

    public GenericDeserializer(Class<T> targetClass) {
        this.objectMapper = new ObjectMapper();
        this.targetClass = targetClass;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
            return Try.of(() -> objectMapper.readValue(bytes, targetClass))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize " + targetClass.getSimpleName(), e));
    }
}