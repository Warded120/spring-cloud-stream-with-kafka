package com.ihren.processor.processor;

import com.ihren.processor.messaging.CustomMessageBuilder;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<Message<InputTransaction>, Message<OutputTransaction>> {
    private final CommonValidator<InputTransaction> validator;
    private final TransactionMapper mapper;
    private final CustomMessageBuilder messageBuilder;

    @Override
    public Message<OutputTransaction> apply(Message<InputTransaction> message) {
        return Optional.of(message)
                .map(Message::getPayload)
                .map(validator::validate)
                .map(mapper::map)
                .map(transaction -> messageBuilder.build(transaction, message.getHeaders()))
                .orElse(null);
    }
}
