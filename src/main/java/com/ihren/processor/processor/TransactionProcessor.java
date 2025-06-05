package com.ihren.processor.processor;

import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;

//TODO: remove exception handler and use ErrorHandler with ContainerCustomizer
@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<Message<InputTransaction>, Message<OutputTransaction>> {
    private final CommonValidator<InputTransaction> validator;
    private final TransactionMapper mapper;

    @Override
    public Message<OutputTransaction> apply(Message<InputTransaction> message) {
        return Optional.of(message)
                .map(Message::getPayload)
                .map(validator::validate)
                .map(mapper::map)
                .map(this::constructMessage)
                .orElse(null);    }

    private Message<OutputTransaction> constructMessage(OutputTransaction outputTransaction) {
        return MessageBuilder
                .withPayload(outputTransaction)
                .build();
    }
}
