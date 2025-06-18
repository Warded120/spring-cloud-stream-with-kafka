package com.ihren.processor.config;

import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.response.ItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import java.util.Optional;

@Configuration
public class CacheConfig {

    @Value("${cache.item.expiration}")
    private Duration ttl;

    @Bean
    public GenericCache<Long, Optional<ItemResponse>> genericCache() {
        return new GenericCache<>(ttl);
    }
}