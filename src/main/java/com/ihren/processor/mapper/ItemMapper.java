package com.ihren.processor.mapper;

import com.ihren.processor.constant.Account;
import com.ihren.processor.model.input.InputItem;
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
public abstract class ItemMapper {
    @Mapping(target = "account", source = "loyaltyAccountId", qualifiedByName = "mapAccount")
    public abstract Item map(InputItem dto);

    @Named("mapAccount")
    protected String mapAccount(CharSequence loyaltyAccountId) {
        return Try.of(() -> Account.getNameById(loyaltyAccountId.toString()))
                .getOrElseThrow(() ->
                        new MappingException(
                                "Unexpected value: " + loyaltyAccountId + ". expected values are: " + Arrays.toString(Account.values())
                        )
                );
    }
}
