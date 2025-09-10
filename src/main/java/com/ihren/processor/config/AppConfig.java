package com.ihren.processor.config;

import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.processor.TransactionProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import java.util.function.Function;

@Configuration
@EnableFeignClients("com.ihren.processor.client")
public class AppConfig {
    @Bean
    public Function<Message<InputTransaction>, Message<OutputTransaction>> processTransaction(TransactionProcessor processor) {
        return processor;
    }

    @Bean
    public Function<Message<InputTransaction>, Message<OutputTransaction>> replayTransaction(TransactionProcessor processor) {
        return processor;
    }
}
