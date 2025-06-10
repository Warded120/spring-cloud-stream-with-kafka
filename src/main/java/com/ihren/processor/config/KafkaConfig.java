package com.ihren.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.CustomKafkaHeaders;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.dlt.customizer.CommonDltCustomizer;
import com.ihren.processor.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Configuration
public class KafkaConfig {

    @Bean
    public ListenerContainerWithDlqAndRetryCustomizer dltCustomizer(CommonErrorHandler errorHandler) {
        return new CommonDltCustomizer(errorHandler);
    }

    @Bean
    public CommonErrorHandler errorHandler(DeadLetterPublishingRecoverer dlpr, BackOff backOff) {
        return new DefaultErrorHandler(dlpr, backOff);
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(
            KafkaTemplate<?, ?> kafkaTemplate,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            ObjectMapper mapper
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, dlqDestinationResolver);
        recoverer.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) -> {
            Try.run(() -> {
                kafkaHeaders.add(CustomKafkaHeaders.ERROR_CODE, mapper.writeValueAsBytes(getCauseFromException(exception)));
                kafkaHeaders.add(CustomKafkaHeaders.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(exception.getMessage()));

                kafkaHeaders.add(CustomKafkaHeaders.IS_DLT, mapper.writeValueAsBytes(true));
            }).getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
        });
        return recoverer;
    }

    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver() {
        return (record, ex) -> new TopicPartition(record.topic().concat(".dlt"), 0);
    }

    @Bean
    public BackOff backOff() {
        return new FixedBackOff(1000L, 1);
    }

    private ErrorCode getCauseFromException(Exception exception) {
        return Stream.iterate(exception, Objects::nonNull, ex -> (Exception) ex.getCause())
                .filter(cause -> ErrorCode.from(cause) != ErrorCode.UNKNOWN_EXCEPTION)
                .map(ErrorCode::from)
                .findFirst()
                .orElse(ErrorCode.UNKNOWN_EXCEPTION);
    }
}
