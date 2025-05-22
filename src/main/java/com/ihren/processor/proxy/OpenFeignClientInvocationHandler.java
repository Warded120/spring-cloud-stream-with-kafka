package com.ihren.processor.proxy;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ihren.processor.client.OpenFeignClient;
import com.ihren.processor.client.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

@RequiredArgsConstructor
public class OpenFeignClientInvocationHandler implements InvocationHandler {

    private final OpenFeignClient original;
    private final LoadingCache<Long, Optional<ItemResponse>> cache;

    @Override
    public Optional<ItemResponse> invoke(Object proxy, Method method, Object[] args) {
        return cache.get((Long) args[0]);
    }
}
