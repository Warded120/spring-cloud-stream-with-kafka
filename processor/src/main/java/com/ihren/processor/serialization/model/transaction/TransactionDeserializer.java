package com.ihren.processor.serialization.model.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TransactionDeserializer implements Deserializer<Transaction> {

    private final ObjectMapper objectMapper;

    public TransactionDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }
    @Override
    public Transaction deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, Transaction.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}