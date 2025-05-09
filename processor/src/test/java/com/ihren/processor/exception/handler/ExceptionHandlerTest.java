package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        String input = "input";
        String expected = "expected";
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
        String input = "input";
        Function function = mock();

        given(function.apply(input)).willThrow(RuntimeException.class);

        //when
        Try actual = handler.handle(function, input);

        //then
        assertEquals(null, actual.get());

        then(function).should().apply(input);
        then(sink).should().apply(any());
    }
}