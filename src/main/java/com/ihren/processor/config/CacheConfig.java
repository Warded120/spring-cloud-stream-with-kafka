package com.ihren.processor.config;

import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.response.ItemResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class CacheConfig {
    @Bean
    public GenericCache<Long, ItemResponse> genericCache() {
        return new GenericCache<>(Duration.of(1, ChronoUnit.DAYS));
    }
}