package com.ihren.processor.serialization.model.total;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.model.Total;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TotalDeserializer implements Deserializer<Total> {

    private final ObjectMapper objectMapper;

    public TotalDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public Total deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, Total.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}