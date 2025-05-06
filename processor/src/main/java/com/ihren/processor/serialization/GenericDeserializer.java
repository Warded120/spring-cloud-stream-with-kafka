package com.ihren.processor.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

public class GenericDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper;
    private Class<T> targetClass;

    public GenericDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String className = (String) configs.get("value.deserializer.target.class");
        try {
            this.targetClass = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Failed to load target class", e);
        }
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
            return Try.of(() -> objectMapper.readValue(bytes, targetClass))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize " + targetClass.getSimpleName(), e));
    }
}