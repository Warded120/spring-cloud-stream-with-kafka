package com.ihren.processor.dlt.customizer;

import com.ihren.processor.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class DltCustomizer implements ListenerContainerWithDlqAndRetryCustomizer {

    private final ErrorHandler errorHandler;

    @Override
    public void configure(
            AbstractMessageListenerContainer<?, ?> container,
            String destinationName,
            String group,
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
            BackOff backOff) {
        container.setCommonErrorHandler(
                errorHandler.createErrorHandler(dlqDestinationResolver, backOff)
        );
    }

    @Override
    public boolean retryAndDlqInBinding(String destinationName, String group) {
        return false;
    }
}
