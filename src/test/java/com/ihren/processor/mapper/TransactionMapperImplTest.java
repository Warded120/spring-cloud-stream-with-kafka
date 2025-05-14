package com.ihren.processor.mapper;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.Currency;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Item;
import com.ihren.processor.model.Total;
import com.ihren.processor.model.Transaction;
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
    void should_ReturnTransaction_when_TransactionDtoIsValid() {
        //given
        UUID uuid = UUID.randomUUID();
        Instant instant = Instant.now();
        Item item = new Item(1L, "Main", "beginDateTime", "endDateTime");
        Total total = new Total(BigDecimal.valueOf(360L), Currency.USD);
        Transaction expected = new Transaction(uuid, Constants.SOFTSERVE, null, 1L, instant, List.of(item), total);

        ItemDto itemDto = mock(ItemDto.class);
        TotalDto totalDto = mock(TotalDto.class);
        TransactionDto transactionDto = mock(TransactionDto.class);
        given(transactionDto.sequenceNumber()).willReturn(1L);
        given(transactionDto.endDateTime()).willReturn(instant.toString());
        given(transactionDto.items()).willReturn(List.of(itemDto));
        given(transactionDto.total()).willReturn(totalDto);

        uuidGenerator.when(() -> UUID.randomUUID()).thenReturn(uuid);
        given(itemMapper.map(itemDto)).willReturn(item);
        given(totalMapper.map(totalDto)).willReturn(total);

        //when
        Transaction actual = transactionMapper.map(transactionDto);

        //then
        assertEquals(expected, actual);

        then(itemMapper).should().map(itemDto);
        then(totalMapper).should().map(totalDto);
    }

    @Test
    void should_ReturnTransactionWithNullItems_when_TransactionDtoItemsIsNull() {
        //given
        UUID uuid = UUID.randomUUID();
        Instant instant = Instant.now();
        Total expectedTotal = new Total(BigDecimal.valueOf(360L), Currency.USD);
        Transaction expected = new Transaction(uuid, Constants.SOFTSERVE, null, 1L, instant, null, expectedTotal);

        TotalDto totalDto = mock(TotalDto.class);
        TransactionDto transactionDto = mock(TransactionDto.class);
        given(transactionDto.sequenceNumber()).willReturn(1L);
        given(transactionDto.endDateTime()).willReturn(instant.toString());
        given(transactionDto.items()).willReturn(null);
        given(transactionDto.total()).willReturn(totalDto);

        uuidGenerator.when(() -> UUID.randomUUID()).thenReturn(uuid);
        given(totalMapper.map(totalDto)).willReturn(expectedTotal);

        //when
        Transaction actual = transactionMapper.map(transactionDto);

        //then
        assertEquals(expected, actual);

        then(totalMapper).should().map(totalDto);
    }

    @Test
    void should_ReturnNull_when_TransactionDtoIsNull() {
        //when
        //then
        assertNull(transactionMapper.map(null));
    }
}