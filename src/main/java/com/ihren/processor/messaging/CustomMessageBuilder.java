package com.ihren.processor.messaging;

import com.ihren.processor.constant.Constants;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CustomMessageBuilder {
    public <T> Message<T> build(T payload, MessageHeaders headers) {
        MessageBuilder<T> builder = MessageBuilder
                .withPayload(payload);

        Optional.ofNullable(headers.get(Constants.Kafka.Headers.IS_DLT))
                .ifPresent(header -> builder.setHeader(Constants.Kafka.Headers.IS_DLT, header));

        return builder.build();
    }
}
