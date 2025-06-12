package com.ihren.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.serializer.JsonDeserializer;
import com.ihren.processor.serializer.JsonSerializer;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

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
            KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate,
            KafkaTemplate<String, byte[]> byteArrayKafkaTemplate,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            ObjectMapper mapper
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                (producerRecord ->
                        producerRecord.value() instanceof InputTransaction
                                ? inputTransactionKafkaTemplate
                                : byteArrayKafkaTemplate
                ),
                dlqDestinationResolver
        );
        recoverer.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) -> {
            Try.run(() -> {
                kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(getErrorCodeFrom(exception)));
                kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(exception.getMessage()));

                kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
            }).getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex, ErrorCode.SERIALIZATION_EXCEPTION));
        });
        //TODO: investigate and maybe use this instead of setExceptionHeadersCreator
        //recoverer.setHeadersFunction();
        return recoverer;
    }

    //TODO: configure via application.yml
    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver() {
        //TODO: don't assign hardcoded partitions (usually dlt-topic partitiond count is smaller that topic partition count)
        return (record, ex) -> new TopicPartition(record.topic().concat(".dlt"), record.partition());
    }

    //TODO: configure in application.yml
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

    @Bean
    public KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate() {
        DefaultKafkaProducerFactory<String, InputTransaction> factory =
                new DefaultKafkaProducerFactory<>(
                        Map.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                        )
                );

        return new KafkaTemplate<>(factory);
    }

    @Bean
    public KafkaTemplate<String, byte[]> byteArrayKafkaTemplate() {
        DefaultKafkaProducerFactory<String, byte[]> factory =
                new DefaultKafkaProducerFactory<>(
                        Map.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
                        )
                );

        return new KafkaTemplate<>(factory);
    }
}
