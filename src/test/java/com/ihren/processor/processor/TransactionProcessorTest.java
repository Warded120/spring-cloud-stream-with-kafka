package com.ihren.processor.processor;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.messaging.CustomMessageBuilder;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.validator.CommonValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {
    @InjectMocks
    private TransactionProcessor processor;

    @Mock
    private CommonValidator<InputTransaction> validator;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private CustomMessageBuilder messageBuilder;

    @Test
    void should_processTransaction_when_EverythingIsOK() {
        //given
        InputTransaction inputTransaction = mock(InputTransaction.class);

        Message<InputTransaction> inputTransactionMessage = mock(Message.class);
        given(inputTransactionMessage.getPayload()).willReturn(inputTransaction);

        MessageHeaders messageHeaders = mock(MessageHeaders.class);
        given(inputTransactionMessage.getHeaders()).willReturn(messageHeaders);

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
        OutputTransaction expectedTransaction = new OutputTransaction(uuid, Constants.Model.SOFTSERVE, null, 1L, instant, List.of(expectedItem), expectedTotal);

        Message<OutputTransaction> expected = MessageBuilder
                .withPayload(expectedTransaction)
                .setHeader(Constants.Kafka.Headers.IS_DLT, Boolean.FALSE)
                .build();

        given(validator.validate(inputTransaction)).willReturn(inputTransaction);
        given(mapper.map(inputTransaction)).willReturn(expectedTransaction);
        given(messageBuilder.build(expectedTransaction, messageHeaders)).willReturn(expected);

        //when
        Message<OutputTransaction> actual = processor.apply(inputTransactionMessage);

        //then
        assertEquals(expected.getPayload(), actual.getPayload());
        assertEquals(expected.getHeaders().get(Constants.Kafka.Headers.IS_DLT), actual.getHeaders().get(Constants.Kafka.Headers.IS_DLT));
    }
}