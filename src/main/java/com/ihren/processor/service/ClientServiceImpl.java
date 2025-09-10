package com.ihren.processor.service;

import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ItemClient itemClient;
    private final CommonValidator<ItemResponse> validator;

    @Override
    public ItemResponse getByItemId(Long id) {
        return itemClient.getById(id)
                .map(validator::validate)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("not found by item.id: %d", id)
                        )
                );
    }
}
