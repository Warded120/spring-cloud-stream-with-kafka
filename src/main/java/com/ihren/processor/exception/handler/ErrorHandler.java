package com.ihren.processor.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import com.ihren.processor.model.input.InputTransaction;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ErrorHandler {
    private final KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate;
    private final KafkaTemplate<String, byte[]> byteArrayKafkaTemplate;
    private final ObjectMapper mapper;

    //TODO: move to DltCustomizer
    public CommonErrorHandler createErrorHandler(
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            BackOff backOff
    ) {
        DeadLetterPublishingRecoverer recoverer = createRecoverer(dlqDestinationResolver);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    //TODO: how to make it a bean
    private DeadLetterPublishingRecoverer createRecoverer(BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(this::templateResolver, dlqDestinationResolver);
        recoverer.setExceptionHeadersCreator(this::headersCreator);
        return recoverer;
    }

    //TODO: how to make it a bean
    private KafkaOperations<?, ?> templateResolver(ProducerRecord<?, ?> record) {
        return record.value() instanceof InputTransaction
                ? inputTransactionKafkaTemplate
                : byteArrayKafkaTemplate;
    }

    //TODO: how to make it a bean
    //TODO: create class implementation of ExceptionHeadersCreator
    private void headersCreator(Headers kafkaHeaders, Exception exception, boolean isKey, DeadLetterPublishingRecoverer.HeaderNames headerNames) {
        Try.run(() -> {
                    ApplicationException applicationException = getApplicationExceptionFrom(exception);

                    kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(applicationException.getErrorCode()));
                    kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(applicationException.getMessage()));
                    kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
                })
                .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
    }

    private ApplicationException getApplicationExceptionFrom(Exception exception) {
        return Optional.of(exception)
                .filter(ApplicationException.class::isInstance)
                .map(ApplicationException.class::cast)
                .orElseGet(() ->
                        Optional.of(exception)
                                .map(ex -> (Exception) ex.getCause())
                                .map(this::getApplicationExceptionFrom)
                                .orElse(new ApplicationException(exception.getMessage(), ErrorCode.UNKNOWN))
                );
    }
}
