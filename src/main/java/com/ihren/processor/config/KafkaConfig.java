package com.ihren.processor.config;

import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.serializer.JsonDeserializer;
import com.ihren.processor.serializer.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.group}")
    private String groupId;

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

    @Bean
    public Function<ProducerRecord<?, ?>, KafkaOperations<?, ?>> templateResolver(
            KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate,
            KafkaTemplate<String, byte[]> byteArrayKafkaTemplate
    ) {
        return record -> record.value() instanceof InputTransaction
                ? inputTransactionKafkaTemplate
                : byteArrayKafkaTemplate;

    }
}
