package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerTest {
    @InjectMocks
    private ExceptionHandler handler;

    @Mock
    private Sink<String> sink;

    @Test
    void should_ApplyFunction_when_InputIsValid() {
        //given
        Message<String> input = MessageBuilder.withPayload("input").build();
        Message<String> expected = MessageBuilder.withPayload("expected").build();
        Function function = mock();

        given(function.apply(input)).willReturn(expected);

        //when
        Try actual = handler.handle(function, input);

        //then
        assertEquals(expected, actual.get());

        then(function).should().apply(input);
    }

    @Test
    void should_ReturnNull_when_RecoverFromException() {
        //given
        Message<String> input = MessageBuilder.withPayload("input").build();
        Function function = mock();

        given(function.apply(input)).willThrow(new RuntimeException("error"));

        //when
        Try actual = handler.handle(function, input);

        //then
        assertEquals(null, actual.get());

        then(function).should().apply(input);
        then(sink).should().apply(any(), any());
    }
}