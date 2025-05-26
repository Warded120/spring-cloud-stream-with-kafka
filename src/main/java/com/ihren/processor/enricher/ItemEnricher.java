package com.ihren.processor.enricher;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.service.ClientService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ItemEnricher implements Enricher<OutputItem> {
    private ClientService clientService;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void enrich(OutputItem item) {
        ItemResponse itemResponse = clientService.getByItemId(item.getId());
        enrich(item, itemResponse);
    }

    protected abstract void enrich(@MappingTarget OutputItem outputItem, ItemResponse itemResponse);
}