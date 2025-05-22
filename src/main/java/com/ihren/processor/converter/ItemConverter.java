package com.ihren.processor.converter;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
//TODO: move this logic to Enricher
public interface ItemConverter {
    @Mapping(target = ".", source = "outputItem")
    @Mapping(target = "price", source = "itemResponse.price")
    @Mapping(target = "producer", source = "itemResponse.producer")
    @Mapping(target = "description", source = "itemResponse.description")
    @Mapping(target = "VATRate", source = "itemResponse.VATRate")
    @Mapping(target = "UOM", source = "itemResponse.UOM")
    @Mapping(target = "BarCode", source = "itemResponse.BarCode")
    OutputItem convert(OutputItem outputItem, ItemResponse itemResponse);
}
