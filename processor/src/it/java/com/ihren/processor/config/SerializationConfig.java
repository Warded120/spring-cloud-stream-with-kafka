package com.ihren.processor.config;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.serialization.GenericDeserializer;
import com.ihren.processor.serialization.GenericSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig {
    @Bean
    public StringSerializer keySerializer() {
        return new StringSerializer();
    }

    @Bean
    public StringDeserializer keyDeserializer() {
        return new StringDeserializer();
    }

    @Bean
    public GenericSerializer<TransactionDto> transactionDtoSerializer() {
        return new GenericSerializer<>();
    }

    @Bean
    GenericDeserializer<TransactionDto> transactionDtoDeserializer() {
        return new GenericDeserializer<>(TransactionDto.class);
    }

    @Bean
    public GenericSerializer<Transaction> transactionSerializer() {
        return new GenericSerializer<>();
    }

    @Bean
    TransactionDeserializer transactionDeserializer() {
        TransactionDeserializer transactionDeserializer = new TransactionDeserializer();
        transactionDeserializer.configure(null, false);
        return transactionDeserializer;
    }
}
