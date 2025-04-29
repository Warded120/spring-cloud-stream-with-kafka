package com.ihren.processor.config;

import com.ihren.misc.serialization.PersonDeserializer;
import com.ihren.model.Person;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.Map;

@TestConfiguration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public KafkaConsumer<String, Person> kafkaConsumer() {
        Map<String, Object> configs = Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new KafkaConsumer<>(
                configs,
                new StringDeserializer(),
                new PersonDeserializer()
        );
    }
}
