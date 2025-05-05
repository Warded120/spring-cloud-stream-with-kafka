package com.ihren.processor.mapper;

import com.ihren.processor.constant.Currency;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.model.Total;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TotalMapper {

    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrency")
    Total map(TotalDto dto);

    @Named("mapCurrency")
    default Currency mapCurrency(String currency) {
        return Currency.valueOf(currency);
    }
}
