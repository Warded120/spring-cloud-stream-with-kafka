package com.ihren.processor.serialization.dto.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TransactionDtoDeserializer implements Deserializer<TransactionDto> {

    private final ObjectMapper objectMapper;

    public TransactionDtoDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }
    @Override
    public TransactionDto deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, TransactionDto.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}