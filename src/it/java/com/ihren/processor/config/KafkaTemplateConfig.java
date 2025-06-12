package com.ihren.processor.config;

import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.serializer.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.Map;

@TestConfiguration
public class KafkaTemplateConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

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