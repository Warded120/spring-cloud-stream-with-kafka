package com.ihren.processor.mapper;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.time.Instant;
import java.util.UUID;

@Mapper(
        componentModel = ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class, TotalMapper.class}
)
public interface TransactionMapper {
    @Mapping(target = "transactionId", source = ".", qualifiedByName = "generateTransactionId")
    @Mapping(target = "source", constant = "Softserve")
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "operationDateTime", source = "endDateTime", qualifiedByName = "parseOperationDateTime")
    Transaction map(TransactionDto dto);

    @Named("generateTransactionId")
    default UUID generateTransactionId(TransactionDto dto) {
        return UUID.randomUUID();
    }

    @Named("parseOperationDateTime")
    default Instant parseOperationDateTime(String endDateTime) {
        return Instant.parse(endDateTime);
    }
}