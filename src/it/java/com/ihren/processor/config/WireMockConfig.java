package com.ihren.processor.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {
    @Bean
    public WireMockConfigurationCustomizer wireMockConfigurationCustomizer() {
        return config -> {
            config.withRootDirectory("src/it/resources");
        };
    }
}