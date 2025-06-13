package com.ihren.processor;

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
public class ErrorHandlerFactory {
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
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(this::templateResolver, dlqDestinationResolver);
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
                .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex, ErrorCode.SERIALIZATION_EXCEPTION));
    }

    private void addHeaders(Headers kafkaHeaders, Exception exception) throws JsonProcessingException {
        kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(getErrorCodeFrom(exception)));
        kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(exception.getMessage()));
        kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
    }

    private ErrorCode getErrorCodeFrom(Exception exception) {
        return Optional.of(exception)
                .filter(e1 -> e1 instanceof ApplicationException)
                .map(e2 -> (ApplicationException) e2)
                .map(ApplicationException::getErrorCode)
                .orElseGet(() ->
                        Optional.of(exception)
                                .map(e4 -> (Exception) e4.getCause())
                                .map(this::getErrorCodeFrom)
                                .orElse(ErrorCode.UNKNOWN_EXCEPTION)
                );
    }
}
