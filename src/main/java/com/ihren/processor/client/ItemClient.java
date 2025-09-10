package com.ihren.processor.client;

import com.ihren.processor.client.response.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@FeignClient(
        name = "nonCacheableItemClient",
        url = "${api.product.url}",
        primary = false,
        qualifiers = "nonCacheableItemClient",
        dismiss404 = true //return Optional.empty() and don't throw FeignException$NptFound if status is 404
)
public interface ItemClient {
    @GetMapping("/users/{id}")
    Optional<ItemResponse> getById(@PathVariable("id") Long id);
}