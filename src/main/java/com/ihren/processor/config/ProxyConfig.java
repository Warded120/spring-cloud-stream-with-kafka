package com.ihren.processor.config;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ihren.processor.client.OpenFeignClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.proxy.OpenFeignClientInvocationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.lang.reflect.Proxy;
import java.util.Optional;

@Configuration
public class ProxyConfig {
    @Bean("openFeignClientProxy")
    public OpenFeignClient openFeignClientProxy(OpenFeignClient client, LoadingCache<Long, Optional<ItemResponse>> cache) {
        return (OpenFeignClient) Proxy.newProxyInstance(
                client.getClass().getClassLoader(),
                client.getClass().getInterfaces(),
                new OpenFeignClientInvocationHandler(cache)
        );
    }
}
