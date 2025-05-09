package com.ihren.processor.processor;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.exception.handler.ExceptionHandler;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.validation.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<TransactionDto, Transaction> {
    private final TransactionMapper mapper;
    private final CommonValidator<TransactionDto> validator;
    private final ExceptionHandler<TransactionDto, Transaction> exceptionHandler;

    @Override
    public Transaction apply(TransactionDto dto) {
        return exceptionHandler.handle(item -> {
                validator.validate(dto);
                return mapper.map(dto);
            },
            dto)
            .get();
    }
}
