package com.ihren.processor.processor;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.exception.handler.ExceptionHandler;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.validation.CommonValidator;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {
    @InjectMocks
    private TransactionProcessor processor;

    @Mock
    private ExceptionHandler<InputTransaction, OutputTransaction> exceptionHandler;

    @Mock
    private CommonValidator<InputTransaction> validator;

    @Mock
    private TransactionMapper mapper;

    @Captor
    private ArgumentCaptor<Function<InputTransaction, OutputTransaction>> captor;

    @Test
    void should_processTransaction_when_EverythingIsOK() {
        //given
        InputTransaction inputTransaction = mock(InputTransaction.class);

        Message<InputTransaction> inputTransactionMessage = mock(Message.class);
        given(inputTransactionMessage.getPayload()).willReturn(inputTransaction);

        Try<OutputTransaction> outputTransactionTry = mock(Try.class);

        UUID uuid = UUID.randomUUID();
        Instant instant = Instant.now();

        OutputItem expectedItem = new OutputItem(
                1L,
                "Main",
                "beginDateTime",
                "endDateTime",
                new BigDecimal("150.00"),
                "producer",
                "description",
                new BigDecimal("150.00"),
                "UOM",
                "12345678901234"
        );
        OutputTotal expectedTotal = new OutputTotal(BigDecimal.valueOf(360L), CurrencyCode.USD);
        OutputTransaction expectedTransaction = new OutputTransaction(uuid, Constants.SOFTSERVE, null, 1L, instant, List.of(expectedItem), expectedTotal);


        Message<OutputTransaction> expected = MessageBuilder
                .withPayload(expectedTransaction)
                .build();

        given(outputTransactionTry.get()).willReturn(expectedTransaction);
        given(validator.validate(inputTransaction)).willReturn(inputTransaction);
        given(mapper.map(inputTransaction)).willReturn(expectedTransaction);

        given(exceptionHandler.handle(captor.capture(), eq(inputTransaction)))
                .willReturn(outputTransactionTry);

        //when
        Message<OutputTransaction> actual = processor.apply(inputTransactionMessage);

        //then
        assertEquals(expected.getPayload(), actual.getPayload());

        Function<InputTransaction, OutputTransaction> captured = captor.getValue();

        OutputTransaction applied = captured.apply(inputTransaction);
        assertEquals(expectedTransaction, applied);
    }
}