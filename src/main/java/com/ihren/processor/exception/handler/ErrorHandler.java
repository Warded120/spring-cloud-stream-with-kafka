package com.ihren.processor.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public CommonErrorHandler createErrorHandler(
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            BackOff backOff
    ) {

        DeadLetterPublishingRecoverer recoverer = createRecoverer(dlqDestinationResolver);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    private DeadLetterPublishingRecoverer createRecoverer(BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver) {
        //TODO: can I use StreamBridge instead of KafkaTemplate to send records
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(this::templateResolver, dlqDestinationResolver);
        //TODO: crate interface implementation of ExceptionHeadersCreator (it's a functional interface)
        recoverer.setExceptionHeadersCreator(this::headersCreator);
        return recoverer;
    }

    private KafkaOperations<?, ?> templateResolver(ProducerRecord<?, ?> record) {
        return record.value() instanceof InputTransaction
                ? inputTransactionKafkaTemplate
                : byteArrayKafkaTemplate;
    }

    private void headersCreator(Headers kafkaHeaders, Exception exception, boolean isKey, DeadLetterPublishingRecoverer.HeaderNames headerNames) {
        Try.run(() -> addHeaders(kafkaHeaders, exception))
                .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
    }

    private void addHeaders(Headers kafkaHeaders, Exception exception) throws JsonProcessingException {
        ApplicationException applicationException = getApplicationExceptionFrom(exception);

        kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(applicationException.getErrorCode()));
        kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(applicationException.getMessage()));
        kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
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
