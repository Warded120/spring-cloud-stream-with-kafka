package com.ihren.processor.messaging;

import com.ihren.processor.constant.Constants;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class CustomMessageBuilder {

    public <T> Message<T> build(T payload, MessageHeaders headers) {
        MessageBuilder<T> builder = MessageBuilder
                .withPayload(payload);

        setCustomHeadersIfExist(builder, headers);

        return builder.build();
    }

    private <T> void setCustomHeadersIfExist(MessageBuilder<T> builder, MessageHeaders original) {
        if (original.containsKey(Constants.Kafka.Headers.IS_DLT)) {
            builder.setHeader(Constants.Kafka.Headers.IS_DLT, original.get(Constants.Kafka.Headers.IS_DLT));
        }

        if (original.containsKey(Constants.Kafka.Headers.EXCEPTION_MESSAGE)) {
            builder.setHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE, original.get(Constants.Kafka.Headers.EXCEPTION_MESSAGE));
        }

        if (original.containsKey(Constants.Kafka.Headers.ERROR_CODE)) {
            builder.setHeader(Constants.Kafka.Headers.ERROR_CODE, original.get(Constants.Kafka.Headers.ERROR_CODE));
        }
    }
}
