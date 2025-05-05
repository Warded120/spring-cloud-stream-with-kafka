package com.ihren.processor.serialization.dto.total;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TotalDtoDeserializer implements Deserializer<TotalDto> {

    private final ObjectMapper objectMapper;

    public TotalDtoDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public TotalDto deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, TotalDto.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}