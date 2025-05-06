package com.ihren.processor.config;

import com.ihren.processor.model.Transaction;
import com.ihren.processor.serialization.GenericDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.Map;

@TestConfiguration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.application.name}")
    private String groupId;

    @Bean
    public KafkaConsumer<String, /*TODO: verify*/ Transaction> kafkaConsumer(
            StringDeserializer keyDeserializer,
            GenericDeserializer<Transaction> valueDeserializer
    ) {
        Map<String, Object> configs = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId
        );

        return new KafkaConsumer<>(configs, keyDeserializer, valueDeserializer);
    }
}
