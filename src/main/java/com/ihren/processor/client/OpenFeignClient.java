package com.ihren.processor.client;

import com.ihren.processor.client.response.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@Primary
@FeignClient(
        value = "api",
        url = "${feign.client.baseUrl}"
)
public interface OpenFeignClient {
    @GetMapping("/users/{id}")
    Optional<ItemResponse> getById(@PathVariable("id") Long id);
}