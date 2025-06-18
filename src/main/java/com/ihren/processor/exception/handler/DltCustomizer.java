package com.ihren.processor.exception.handler;

import com.ihren.processor.model.input.InputTransaction;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class DltCustomizer implements ListenerContainerWithDlqAndRetryCustomizer {
    private final KafkaTemplate<String, InputTransaction> inputTransactionKafkaTemplate;
    private final KafkaTemplate<String, byte[]> byteArrayKafkaTemplate;
    private final ExceptionHeaderHandler headersCreator;

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
                        new FixedBackOff(0, 1)
                )
        );
    }

    @Override
    public boolean retryAndDlqInBinding(String destinationName, String group) {
        return false;
    }

    private DeadLetterPublishingRecoverer createRecoverer(BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(this::templateResolver, dlqDestinationResolver);
        recoverer.setExceptionHeadersCreator(headersCreator);
        return recoverer;
    }

    private KafkaOperations<?, ?> templateResolver(ProducerRecord<?, ?> record) {
        return record.value() instanceof InputTransaction
                ? inputTransactionKafkaTemplate
                : byteArrayKafkaTemplate;
    }
}
