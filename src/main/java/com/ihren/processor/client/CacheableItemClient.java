package com.ihren.processor.client;

import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("itemClient")
@RequiredArgsConstructor
public class CacheableItemClient implements ItemClient {
    private final ItemClient originalItemClient;
    private final GenericCache<Long, ItemResponse> cache;

    @Override
    public ItemResponse getById(Long id) {
        return cache
                .of(originalItemClient::getById)
                .apply(id);
    }
}
