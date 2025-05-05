package com.ihren.processor.processor;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Transaction;
import jakarta.validation.Valid;
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
    public Transaction apply(@Valid TransactionDto dto) {
        return mapper.map(dto);
    }
}
