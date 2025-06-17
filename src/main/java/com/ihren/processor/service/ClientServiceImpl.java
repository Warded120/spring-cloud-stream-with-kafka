package com.ihren.processor.service;

import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.validator.CommonValidator;
import feign.FeignException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ItemClient itemClient;
    private final CommonValidator<ItemResponse> validator;

    @Override
    public ItemResponse getByItemId(Long id) {
        //TODO: itemClient.getById(id) should return optional
        //TODO: use opt.of().map(validate).orElseThrow(...)
        return Try.of(() -> itemClient.getById(id))
                .map(validator::validate)
                .recoverWith(
                        FeignException.class,
                        ex ->
                                Try.failure(
                                        new NotFoundException(
                                                String.format("not found by item.id: %d", id),
                                                ex
                                        )
                                )
                )
                .get();
    }
}
