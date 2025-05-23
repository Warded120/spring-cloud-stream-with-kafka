package com.ihren.processor.proxy;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ihren.processor.client.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

@RequiredArgsConstructor
public class OpenFeignClientInvocationHandler implements InvocationHandler {

    private final LoadingCache<Long, Optional<ItemResponse>> cache;

    //TODO: write unit tests
    //TODO: what if the interface has more than one method? (how to control what logic to run depending in the method that is called)
    @Override
    public Optional<ItemResponse> invoke(Object proxy, Method method, Object[] args) {
        return cache.get((Long) args[0]);
    }
}
