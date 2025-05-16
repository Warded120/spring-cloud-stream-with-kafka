package com.ihren.processor.mapper;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.model.input.InputTotal;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.output.OutputItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TransactionMapperImplTest {
    @Spy
    @InjectMocks
    private TransactionMapperImpl transactionMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private TotalMapper totalMapper;

    private MockedStatic<UUID> uuidGenerator;

    @BeforeEach
    void setUp() {
        uuidGenerator = mockStatic(UUID.class);
    }

    @AfterEach
    void tearDown() {
        uuidGenerator.close();
    }

    @Test
    void should_ReturnOutputTransaction_when_InputTransactionIsValid() {
        //given
        UUID uuid = UUID.randomUUID();
        Instant instant = Instant.now();
        OutputItem expectedItem = new OutputItem(1L, "Main", "beginDateTime", "endDateTime");
        OutputTotal expectedTotal = new OutputTotal(BigDecimal.valueOf(360L), CurrencyCode.USD);
        OutputTransaction expectedTransaction = new OutputTransaction(uuid, Constants.SOFTSERVE, null, 1L, instant, List.of(expectedItem), expectedTotal);

        InputItem inputItem = mock(InputItem.class);
        InputTotal inputTotal = mock(InputTotal.class);
        InputTransaction inputTransaction = mock(InputTransaction.class);
        given(inputTransaction.sequenceNumber()).willReturn(1L);
        given(inputTransaction.endDateTime()).willReturn(instant.toString());
        given(inputTransaction.items()).willReturn(List.of(inputItem));
        given(inputTransaction.total()).willReturn(inputTotal);

        uuidGenerator.when(UUID::randomUUID).thenReturn(uuid);
        given(itemMapper.map(inputItem)).willReturn(expectedItem);
        given(totalMapper.map(inputTotal)).willReturn(expectedTotal);

        //when
        OutputTransaction actual = transactionMapper.map(inputTransaction);

        //then
        assertEquals(expectedTransaction, actual);

        then(itemMapper).should().map(inputItem);
        then(totalMapper).should().map(inputTotal);
    }

    @Test
    void should_ReturnOutputTransactionWithNullItems_when_InputTransactionItemsIsNull() {
        //given
        UUID uuid = UUID.randomUUID();
        Instant instant = Instant.now();
        OutputTotal expectedTotal = new OutputTotal(BigDecimal.valueOf(360L), CurrencyCode.USD);
        OutputTransaction expectedTransaction = new OutputTransaction(uuid, Constants.SOFTSERVE, null, 1L, instant, null, expectedTotal);

        InputTotal inputTotal = mock(InputTotal.class);
        InputTransaction inputTransaction = mock(InputTransaction.class);
        given(inputTransaction.sequenceNumber()).willReturn(1L);
        given(inputTransaction.endDateTime()).willReturn(instant.toString());
        given(inputTransaction.items()).willReturn(null);
        given(inputTransaction.total()).willReturn(inputTotal);

        uuidGenerator.when(UUID::randomUUID).thenReturn(uuid);
        given(totalMapper.map(inputTotal)).willReturn(expectedTotal);

        //when
        OutputTransaction actual = transactionMapper.map(inputTransaction);

        //then
        assertEquals(expectedTransaction, actual);

        then(totalMapper).should().map(inputTotal);
    }

    @Test
    void should_ReturnNull_when_InputTransactionIsNull() {
        //when
        //then
        assertNull(transactionMapper.map(null));
    }
}