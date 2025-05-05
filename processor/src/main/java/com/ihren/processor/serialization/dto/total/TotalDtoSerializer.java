package com.ihren.processor.serialization.dto.total;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TotalDtoSerializer implements Serializer<TotalDto> {

    private final ObjectMapper objectMapper;

    public TotalDtoSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, TotalDto item) {
        return Try.of(() -> objectMapper.writeValueAsBytes(item))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
