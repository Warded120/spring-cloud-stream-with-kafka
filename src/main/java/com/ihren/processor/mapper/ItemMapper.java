package com.ihren.processor.mapper;

import com.ihren.processor.constant.Account;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.mapper.exception.MappingException;
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
    public abstract OutputItem map(InputItem dto);

    @Named("mapAccount")
    protected String mapAccount(CharSequence loyaltyAccountId) {
        return Account.getNameById(loyaltyAccountId.toString())
                .orElseThrow(() ->
                        new MappingException(
                                String.format(
                                        "Unexpected value: %s. Expected values are: %s",
                                        loyaltyAccountId,
                                        Arrays.toString(Account.values())
                                )
                        )
                );
    }
}
