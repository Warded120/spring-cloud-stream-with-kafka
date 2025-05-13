package com.ihren.processor.mapper;

import com.ihren.processor.constant.Account;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.mapper.exception.MappingException;
import com.ihren.processor.model.Item;
import io.vavr.control.Try;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.Arrays;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemMapper {
    @Mapping(target = "account", source = "loyaltyAccountId", qualifiedByName = "mapAccount")
    Item map(ItemDto dto);

    @Named("mapAccount")
    default String mapAccount(CharSequence loyaltyAccountId) {
        return Try.of(() -> Account.fromId(loyaltyAccountId.toString()))
                .getOrElseThrow(() -> new MappingException("Unexpected value: " + loyaltyAccountId + ". expected values are: " + Arrays.toString(Account.values())));
    }
}
