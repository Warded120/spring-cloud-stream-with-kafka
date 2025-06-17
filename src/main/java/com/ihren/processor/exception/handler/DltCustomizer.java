package com.ihren.processor.exception.handler;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DltCustomizer implements ListenerContainerWithDlqAndRetryCustomizer {

    private final Function<ProducerRecord<?, ?>, KafkaOperations<?, ?>> templateResolver;
    private final ApplicaitonExceptionHeadersCreator headersCreator;

    @Override
    public void configure(
            AbstractMessageListenerContainer<?, ?> container,
            String destinationName,
            String group,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            BackOff backOff) {
        container.setCommonErrorHandler(
                new DefaultErrorHandler(
                        createRecoverer(dlqDestinationResolver),
                        backOff
                )
        );
    }

    @Override
    public boolean retryAndDlqInBinding(String destinationName, String group) {
        return false;
    }

    private DeadLetterPublishingRecoverer createRecoverer(BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(templateResolver, dlqDestinationResolver);
        recoverer.setExceptionHeadersCreator(headersCreator);
        return recoverer;
    }
}
