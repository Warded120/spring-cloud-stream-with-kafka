package com.ihren.processor.mapper;

import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.model.Total;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TotalMapper {

    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrency")
    Total map(TotalDto dto);

    @Named("mapCurrency")
    default CurrencyCode mapCurrency(String currency) {
        return CurrencyCode.valueOf(currency);
    }
}
