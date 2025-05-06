package com.ihren.processor.mapper;

import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.mapper.exception.MappingException;
import com.ihren.processor.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemMapper {
    @Mapping(target = "account", source = "loyaltyAccountId", qualifiedByName = "mapAccount")
    Item map(ItemDto dto);

    @Named("mapAccount")
    default String mapAccount(CharSequence loyaltyAccountId) {
        return switch (loyaltyAccountId.toString()) {
            case "1" -> "Main";
            case "2" -> "Coupon";
            case "3" -> "Base";
            case "4" -> "Total";
            default -> throw new MappingException("Unexpected value: " + loyaltyAccountId + ". expected values are: 1, 2, 3, 4");
        };
    }
}
