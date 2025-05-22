package com.ihren.processor.enricher;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.converter.ItemConverter;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemEnricher implements Enricher<OutputItem> {
    private final ClientService clientService;
    //TODO: enricher itself must use mapStruct
    private final ItemConverter converter;

    @Override
    public OutputItem enrich(OutputItem item) {
        ItemResponse itemResponse = clientService.getByItemId(item.id());
        return doEnrich(item, itemResponse);
    }

    private OutputItem doEnrich(OutputItem item, ItemResponse itemResponse) {
        return converter.convert(item, itemResponse);
    }
}