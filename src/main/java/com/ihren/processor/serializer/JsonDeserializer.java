package com.ihren.processor.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

//TODO: implement sink to send message to dlt add 2 headers: errorCode (Enum) and message
//TODO: Handle exceptions and send message to DLT (inject exceptionHandler bean with ApplicationContextAware)
//TODO: OR
//TODO: Move de/serialization to processor so that processor takes bytes and de/serialize it inside to avoid static field from spring context
public class JsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper;
    private Class<T> targetClass;

    public JsonDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String className = (String) configs.get("value.deserializer.target.class");
        Try.of(() -> this.targetClass = (Class<T>) Class.forName(className))
                .getOrElseThrow(ex -> new SerializationException("Failed to load target class", ex));
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
            return Try.of(() -> objectMapper.readValue(bytes, targetClass))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize " + targetClass, e));
    }
}