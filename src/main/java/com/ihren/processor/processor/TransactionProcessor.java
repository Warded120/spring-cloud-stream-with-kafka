package com.ihren.processor.processor;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.exception.handler.ExceptionHandler;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.validation.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<Message<TransactionDto>, Message<Transaction>> {
    private final TransactionMapper mapper;
    private final CommonValidator<TransactionDto> validator;
    private final ExceptionHandler<TransactionDto, Transaction> exceptionHandler;

    @Override
    public Message<Transaction> apply(Message<TransactionDto> message) {
        return MessageBuilder
                .withPayload(exceptionHandler.handle(this::processTransaction, message).get())
                .build();
    }

    private Transaction processTransaction(Message<TransactionDto> item) {
        return Optional.of(item)
                .map(Message::getPayload)
                .map(validator::validate)
                .map(mapper::map)
                .orElse(null);
    }
}
