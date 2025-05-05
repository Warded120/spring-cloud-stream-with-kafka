package com.ihren.processor.processor;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<TransactionDto, Transaction> {
    private final TransactionMapper mapper;

    //TODO: trigger validation
    //TODO: implement exception handler (if validation failed then return null, implement DLT in future)
    @Override
    public Transaction apply(TransactionDto dto) {
        return mapper.map(dto);
    }
}
