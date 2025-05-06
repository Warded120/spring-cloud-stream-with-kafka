package com.ihren.processor.serialization.transaction;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.serialization.GenericDeserializer;

public class TransactionDtoDeserializer extends GenericDeserializer<TransactionDto> {
    public TransactionDtoDeserializer() {
        super(TransactionDto.class);
    }
}