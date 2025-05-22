package com.ihren.processor.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ihren.processor.client.OpenFeignClient;
import com.ihren.processor.client.response.ItemResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public LoadingCache<Long, Optional<ItemResponse>> itemResponseCache(OpenFeignClient client) {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(client::getById);
    }
}