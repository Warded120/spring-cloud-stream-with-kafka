package com.ihren.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.SerializationException;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.serializer.JsonDeserializer;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Configuration
public class ErrorHandlerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.group}")
    private String groupId;

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
        //TODO: add recovery function to serialize a broken message (in dlt it has to be stored in the same form as it was before sending)
        recoverer.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) -> {
            Try.run(() -> {
                kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(getCauseFromException(exception)));
                kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(exception.getMessage()));

                kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
            }).getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
        });
        //TODO: investigate and maybe use this instead of setExceptionHeadersCreator
        //recoverer.setHeadersFunction();
        return recoverer;
    }

    //TODO: configure via application.yml and don't assign partitions
    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver() {
        return (record, ex) -> new TopicPartition(record.topic().concat(".dlt"), 0);
    }

    //TODO: configure in application.yml
    @Bean
    public BackOff backOff() {
        return new FixedBackOff(1000L, 1);
    }

    //TODO: create ApplicationException(with ErrorCode) and inherit all my exceptions
    private ErrorCode getCauseFromException(Exception exception) {
        return Stream.iterate(exception, Objects::nonNull, ex -> (Exception) ex.getCause())
                .filter(cause -> ErrorCode.from(cause) != ErrorCode.UNKNOWN_EXCEPTION)
                .map(ErrorCode::from)
                .findFirst()
                .orElse(ErrorCode.UNKNOWN_EXCEPTION);
    }

    @Bean
    public KafkaConsumer<String, InputTransaction> dltKafkaConsumer() {
        Map<String, Object> configs = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                "value.deserializer.target.class", InputTransaction.class.getName()
        );

        return new KafkaConsumer<>(configs);
    }
}
