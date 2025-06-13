package com.ihren.processor.dlt.customizer;

import com.ihren.processor.factory.ErrorHandlerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.util.backoff.BackOff;
import java.util.function.BiFunction;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DltCustomizerTest {
    @InjectMocks
    private DltCustomizer dltCustomizer;

    @Mock
    private ErrorHandlerFactory errorHandlerFactory;

    @Test
    void should_ConfigureContainer_when_Configured() {
        //given
        AbstractMessageListenerContainer container = mock(AbstractMessageListenerContainer.class);
        String destinationName = "destination";
        String group = "group";
        CommonErrorHandler errorHandler = mock(CommonErrorHandler.class);
        BiFunction dlqDestinationResolver = mock(BiFunction.class);
        BackOff backOff = mock(BackOff.class);

        given(errorHandlerFactory.createErrorHandler(dlqDestinationResolver, backOff)).willReturn(errorHandler);

        //when
        dltCustomizer.configure(container, destinationName, group, dlqDestinationResolver, backOff);

        //then
        then(container).should().setCommonErrorHandler(errorHandler);
    }
}