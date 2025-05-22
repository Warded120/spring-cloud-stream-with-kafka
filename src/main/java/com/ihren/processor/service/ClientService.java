package com.ihren.processor.service;

import com.ihren.processor.client.response.ItemResponse;

public interface ClientService {
    ItemResponse getByItemId(Long id);
}
