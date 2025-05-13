package com.ihren.processor.mapper;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class, TotalMapper.class}
)
public interface TransactionMapper {
    @Mapping(target = "transactionId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "source", constant = Constants.SOFTSERVE)
    @Mapping(target = "discount", ignore = true)
    //TODO: do I need DateTimeUtils class to have a method that parses Instant?
    @Mapping(target = "operationDateTime", expression = "java(java.time.Instant.parse(dto.endDateTime()))")
    Transaction map(TransactionDto dto);
}