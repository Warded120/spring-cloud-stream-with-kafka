package com.ihren.processor.service;

import com.ihren.processor.client.OpenFeignClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.validation.CommonValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    private final OpenFeignClient client;
    private final CommonValidator<ItemResponse> validator;

    public ClientServiceImpl(
            @Qualifier("openFeignClientProxy") OpenFeignClient client,
            CommonValidator<ItemResponse> validator
    ) {
        this.client = client;
        this.validator = validator;
    }

    @Override
    public ItemResponse getByItemId(Long id) {
        return client.getById(id)
                .map(validator::validate)
                .orElseThrow(() -> new NotFoundException("not found by item.id: " + id));
    }
}
