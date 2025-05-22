package com.ihren.processor.enricher;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.service.ClientService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
//TODO: do I need it?
public abstract class OutputItemEnricher implements Enricher<OutputItem> {
    private ClientService clientService;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Mapping(target = ".", source = "outputItem")
    @Mapping(target = ".", source = "outputItem", qualifiedByName = "populateWithItemResponse")
    public abstract OutputItem enrich(OutputItem outputItem);

    @Named("populateWithItemResponse")
    protected ItemResponse populateWithItemResponse(OutputItem outputItem) {
        return clientService.getByItemId(outputItem.id());
    }
}
