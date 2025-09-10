package com.ihren.processor.dlt.customizer;

import com.ihren.processor.exception.handler.DltHandlerCustomizer;
import com.ihren.processor.kafka.headers.handler.ExceptionHeadersHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import java.util.function.BiFunction;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(MockitoExtension.class)
class DltHandlerCustomizerTest {
    @InjectMocks
    private DltHandlerCustomizer dltHandlerCustomizer;

    @Mock
    private ExceptionHeadersHandler headersFunction;

    private MockedConstruction<DefaultErrorHandler> errorHandlerConstructionMock;
    private MockedConstruction<DeadLetterPublishingRecoverer> deadLetterPublishingRecovererConstructionMock;

    @BeforeEach
    void setUp() {
        errorHandlerConstructionMock = mockConstruction(DefaultErrorHandler.class);
        deadLetterPublishingRecovererConstructionMock = mockConstruction(DeadLetterPublishingRecoverer.class);
    }

    @AfterEach
    void tearDown() {
        errorHandlerConstructionMock.close();
        deadLetterPublishingRecovererConstructionMock.close();
    }

    @Test
    void should_ConfigureContainer_when_Configured() {
        //given
        AbstractMessageListenerContainer container = mock(AbstractMessageListenerContainer.class);
        String destinationName = "destination";
        String group = "group";
        BiFunction dlqDestinationResolver = mock(BiFunction.class);
        BackOff backOff = mock(BackOff.class);

        //when
        dltHandlerCustomizer.configure(container, destinationName, group, dlqDestinationResolver, backOff);

        //then
        Assertions.assertEquals(1, errorHandlerConstructionMock.constructed().size());
        Assertions.assertEquals(1, deadLetterPublishingRecovererConstructionMock.constructed().size());

        DefaultErrorHandler constructedErrorHandler = errorHandlerConstructionMock.constructed().get(0);
        DeadLetterPublishingRecoverer constructedRecoverer = deadLetterPublishingRecovererConstructionMock.constructed().get(0);

        then(container).should().setCommonErrorHandler(constructedErrorHandler);
        then(constructedRecoverer).should().setHeadersFunction(headersFunction);
    }
}