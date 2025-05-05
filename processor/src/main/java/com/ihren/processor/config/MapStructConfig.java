package com.ihren.processor.config;

import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.mapper.TransactionMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapStructConfig {
    @Bean
    @Primary
    public TransactionMapper transactionMapper(TransactionMapperImpl implementation) {
        return implementation;
    }
}