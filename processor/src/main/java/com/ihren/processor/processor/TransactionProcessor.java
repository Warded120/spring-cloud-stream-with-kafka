package com.ihren.processor.processor;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Validated //TODO: do I need this for validation?
public class TransactionProcessor implements Function<TransactionDto, Transaction> {
    private final TransactionMapper mapper;

    @Override
    //TODO: implement/test/debug validation for dtos
    //TODO: maybe Validation.validate(dto) to explicitly call validaiton or use @Valid annotation
    public Transaction apply(TransactionDto dto) {
        return mapper.map(dto);
    }
}
