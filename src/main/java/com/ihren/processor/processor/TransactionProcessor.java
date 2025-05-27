package com.ihren.processor.processor;

import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.exception.handler.ExceptionHandler;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements Function<Message<InputTransaction>, Message<OutputTransaction>> {
    private final ExceptionHandler<InputTransaction, OutputTransaction> exceptionHandler;
    private final CommonValidator<InputTransaction> validator;
    private final TransactionMapper mapper;

    @Override
    public Message<OutputTransaction> apply(Message<InputTransaction> message) {
        return constructMessage(
                exceptionHandler.handle(
                                this::processTransaction,
                                message.getPayload()
                ).get()
        );
    }

    private Message<OutputTransaction> constructMessage(OutputTransaction outputTransaction) {
        return Optional.ofNullable(outputTransaction)
                .map(transaction -> MessageBuilder.withPayload(transaction).build())
                .orElse(null);
    }

    private OutputTransaction processTransaction(InputTransaction item) {
        return Optional.of(item)
                .map(validator::validate)
                .map(mapper::map)
                .orElse(null);
    }
}
