package com.ihren.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import com.ihren.processor.model.input.InputTransaction;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Optional;
import java.util.function.BiFunction;

@Configuration
public class ErrorHandlerConfig {
    @Bean
    public CommonErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer, BackOff backOff) {
        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(
            KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate,
            KafkaTemplate<String, byte[]> byteArrayKafkaTemplate,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            ObjectMapper mapper
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                producerRecord ->
                        producerRecord.value() instanceof InputTransaction
                                ? inputTransactionKafkaTemplate
                                : byteArrayKafkaTemplate
                ,
                dlqDestinationResolver
        );
        recoverer.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) ->
                Try.run(() -> {
                            kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(getErrorCodeFrom(exception)));
                            kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(exception.getMessage()));

                            kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
                        })
                        .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex, ErrorCode.SERIALIZATION_EXCEPTION))
        );
        //TODO: investigate and maybe use this instead of setExceptionHeadersCreator
        //recoverer.setHeadersFunction();
        return recoverer;
    }

    //TODO: remove and use dlqDestinationResolver from DltCustomizer.configure()
    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver() {
        return (record, ex) -> new TopicPartition(record.topic().concat(".dlt"), record.partition());
    }

    //TODO: configure in application.yml (?)
    @Bean
    public BackOff backOff() {
        return new FixedBackOff(1000L, 1);
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
