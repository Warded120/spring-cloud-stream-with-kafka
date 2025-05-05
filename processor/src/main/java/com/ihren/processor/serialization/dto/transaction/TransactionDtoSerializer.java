package com.ihren.processor.serialization.dto.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TransactionDtoSerializer implements Serializer<TransactionDto> {

    private final ObjectMapper objectMapper;

    public TransactionDtoSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, TransactionDto transaction) {
        return Try.of(() -> objectMapper.writeValueAsBytes(transaction))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
