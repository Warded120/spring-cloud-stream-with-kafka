package com.ihren.processor.mapper;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.util.DateTimeUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import java.util.UUID;

@Mapper(
        componentModel = ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class, TotalMapper.class},
        imports = {UUID.class, DateTimeUtils.class}
)
public interface TransactionMapper {
    @Mapping(target = "transactionId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "source", constant = Constants.SOFTSERVE)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "operationDateTime", expression = "java(DateTimeUtils.parseInstant(dto.endDateTime()))")
    Transaction map(InputTransaction dto);
}