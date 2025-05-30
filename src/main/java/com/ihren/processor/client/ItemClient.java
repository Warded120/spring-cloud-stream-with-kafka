package com.ihren.processor.client;

import com.ihren.processor.client.response.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "nonCacheableItemClient",
        url = "${api.itemClient.url}",
        primary = false,
        qualifiers = "nonCacheableItemClient"
)
public interface ItemClient {
    @GetMapping("/users/{id}")
    ItemResponse getById(@PathVariable("id") Long id);
}