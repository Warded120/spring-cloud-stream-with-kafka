package com.ihren.processor.processor;

import com.ihren.processor.dto.InputTransaction;
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
public class TransactionProcessor implements Function<Message<InputTransaction>, Message<Transaction>> {
    private final TransactionMapper mapper;
    private final CommonValidator<InputTransaction> validator;
    private final ExceptionHandler<InputTransaction, Transaction> exceptionHandler;

    @Override
    public Message<Transaction> apply(Message<InputTransaction> message) {
        return MessageBuilder
                .withPayload(exceptionHandler.handle(this::processTransaction, message).get())
                .build();
    }

    private Transaction processTransaction(Message<InputTransaction> item) {
        return Optional.of(item)
                .map(Message::getPayload)
                .map(validator::validate)
                .map(mapper::map)
                .orElse(null);
    }
}
