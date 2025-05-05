package com.ihren.processor.serialization.model.total;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.model.Total;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TotalSerializer implements Serializer<Total> {

    private final ObjectMapper objectMapper;

    public TotalSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, Total item) {
        return Try.of(() -> objectMapper.writeValueAsBytes(item))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
