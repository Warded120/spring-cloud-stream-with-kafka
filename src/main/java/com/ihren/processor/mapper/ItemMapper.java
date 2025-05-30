package com.ihren.processor.mapper;

import com.ihren.processor.constant.Account;
import com.ihren.processor.enricher.Enricher;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.exception.MappingException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ItemMapper {
    private Enricher<OutputItem> enricher;

    @Autowired
    public void setEnricher(Enricher<OutputItem> enricher) {
        this.enricher = enricher;
    }

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

    @AfterMapping
    protected void enrichItem(@MappingTarget OutputItem outputItem) {
        enricher.enrich(outputItem);
    }
}
