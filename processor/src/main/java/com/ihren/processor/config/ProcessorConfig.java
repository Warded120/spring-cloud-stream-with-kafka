package com.ihren.processor.config;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.processor.TransactionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Function;

@Configuration
public class ProcessorConfig {
    //TODO: setup application.yml kafka bindings
    @Bean
    public Function<TransactionDto, Transaction> processTransaction(TransactionProcessor processor) {
        return processor;
    }
}
