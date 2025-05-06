package com.ihren.processor.config;

import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.serialization.GenericSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.Map;

@TestConfiguration
public class KafkaTemplateConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, /*TODO: verify*/ TransactionDto> producerFactory(
            StringSerializer keySerializer,
            GenericSerializer<TransactionDto> valueSerializer
    ) {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getClass(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getClass()
        );

        return new DefaultKafkaProducerFactory<>(config, keySerializer, valueSerializer);
    }

    @Bean
    public KafkaTemplate<String, /*TODO: verify*/ TransactionDto> kafkaTemplate(ProducerFactory<String, /*TODO: verify*/ TransactionDto> factory) {
        return new KafkaTemplate<>(factory);
    }
}