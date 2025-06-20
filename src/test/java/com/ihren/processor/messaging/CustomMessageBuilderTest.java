package com.ihren.processor.messaging;

import com.ihren.processor.constant.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CustomMessageBuilderTest {
    private final CustomMessageBuilder builder = new CustomMessageBuilder();

    @Test
    void should_BuildMessageWithHeaders_when_HeadersArePresent() {
        //given
        Object payload = mock(Object.class);
        MessageHeaders headers = mock(MessageHeaders.class);

        GenericMessage expected = new GenericMessage(
                payload,
                new MessageHeaders(
                        Map.of(
                                Constants.Kafka.Headers.IS_DLT,
                                true
                        )
                )
        );

        given(headers.get(Constants.Kafka.Headers.IS_DLT)).willReturn(true);

        //when
        Message<Object> actual = builder.build(payload, headers);

        //then
        assertEquals(expected.getPayload(), actual.getPayload());
        assertEquals(expected.getHeaders().get(Constants.Kafka.Headers.IS_DLT), actual.getHeaders().get(Constants.Kafka.Headers.IS_DLT));
    }

    @Test
    void should_BuildMessageWithoutHeaders_when_HeadersAreNotPresent() {
        //given
        Object payload = mock(Object.class);
        MessageHeaders headers = mock(MessageHeaders.class);

        GenericMessage expected = new GenericMessage(
                payload,
                new MessageHeaders(null)
        );

        //when
        Message<Object> actual = builder.build(payload, headers);

        //then
        assertEquals(expected.getPayload(), actual.getPayload());
        assertEquals(expected.getHeaders().get(Constants.Kafka.Headers.IS_DLT), actual.getHeaders().get(Constants.Kafka.Headers.IS_DLT));    }
}