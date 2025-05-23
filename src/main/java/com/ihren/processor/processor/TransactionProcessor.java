package com.ihren.processor.processor;

import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.exception.handler.ExceptionHandler;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.validation.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<Message<InputTransaction>, Message<OutputTransaction>> {
    private final TransactionMapper mapper;
    private final CommonValidator<InputTransaction> validator;
    private final ExceptionHandler<InputTransaction, OutputTransaction> exceptionHandler;

    @Override
    public Message<OutputTransaction> apply(Message<InputTransaction> message) {
        return MessageBuilder
                .withPayload(exceptionHandler.handle(this::processTransaction, message).get())
                .build();
    }

    private OutputTransaction processTransaction(Message<InputTransaction> item) {
        return Optional.of(item)
                .map(Message::getPayload)
                .map(validator::validate)
                .map(mapper::map)
                .orElse(null);
    }
}
