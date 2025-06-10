package com.ihren.processor.dlt.customizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.util.backoff.BackOff;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
public class CommonDltCustomizer implements ListenerContainerWithDlqAndRetryCustomizer {

    private final CommonErrorHandler errorHandler;

    @Override
    public void configure(
            AbstractMessageListenerContainer<?, ?> container,
            String destinationName,
            String group,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            BackOff backOff) {
        container.setCommonErrorHandler(errorHandler);
    }

    @Override
    public boolean retryAndDlqInBinding(String destinationName, String group) {
        return false;
    }
}
