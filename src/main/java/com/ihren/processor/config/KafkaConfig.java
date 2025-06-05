package com.ihren.processor.config;

import com.ihren.processor.dlt.customizer.CommonDltCustomizer;
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
import java.util.function.BiFunction;

@Configuration
public class KafkaConfig {

    @Bean
    public ListenerContainerWithDlqAndRetryCustomizer DltCustomizer(CommonErrorHandler errorHandler) {
        return new CommonDltCustomizer(errorHandler);
    }

    @Bean
    public CommonErrorHandler errorHandler(DeadLetterPublishingRecoverer dlpr, BackOff backOff) {
        return new DefaultErrorHandler(dlpr, backOff);
//        return new MyErrorHandler(dlpr, backOff);
    }

    @Bean
    public DeadLetterPublishingRecoverer dlpr(KafkaTemplate<?, ?> kafkaTemplate, BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver) {
        DeadLetterPublishingRecoverer dlpr = new DeadLetterPublishingRecoverer(kafkaTemplate, dlqDestinationResolver);
        dlpr.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) -> {
            //TODO: set errorCode header and keep existing headers
            kafkaHeaders.add("exception", exception.getMessage().getBytes());

            kafkaHeaders.remove("isReply");
            kafkaHeaders.add("isReply", "true".getBytes());
        });
        return dlpr;
    }

    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver() {
        return (record, ex) -> new TopicPartition(record.topic().concat(".dlt"), 0);
    }

    @Bean
    public BackOff backOff() {
        return new FixedBackOff(1000L, 1);
    }
}
