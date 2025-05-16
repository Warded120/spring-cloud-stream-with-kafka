package com.ihren.processor.mapper;

import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.model.input.InputTotal;
import com.ihren.processor.model.output.OutputTotal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TotalMapper {

    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrency")
    public abstract OutputTotal map(InputTotal dto);

    @Named("mapCurrency")
    protected CurrencyCode mapCurrency(String currency) {
        return CurrencyCode.valueOf(currency);
    }
}
