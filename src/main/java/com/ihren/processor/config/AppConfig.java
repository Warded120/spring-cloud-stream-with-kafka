package com.ihren.processor.config;

import com.ihren.processor.dto.InputTransaction;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.processor.TransactionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import java.util.function.Function;

@Configuration
public class AppConfig {
    @Bean
    public Function<Message<InputTransaction>, Message<Transaction>> processTransaction(TransactionProcessor processor) {
        return processor;
    }
}
