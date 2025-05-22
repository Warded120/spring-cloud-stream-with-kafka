package com.ihren.processor.mapper;

import com.ihren.processor.enricher.Enricher;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.mapper.exception.MappingException;
import com.ihren.processor.model.output.OutputItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemMapperImplTest {
    @Spy
    @InjectMocks
    private final ItemMapper itemMapper = new ItemMapperImpl();

    @Mock
    private Enricher<OutputItem> enricher;

    @Test
    void should_ReturnOutputItem_when_InputItemIsValid() {
        //given
        OutputItem expectedAfterEnriching = new OutputItem(
                1L,
                "Main",
                "beginDateTime",
                "endDateTime",
                new BigDecimal("150.00"),
                "producer",
                "description",
                new BigDecimal("150.00"),
                "UOM",
                "BarCode"
        );

        OutputItem expectedBeforeEnriching = new OutputItem(
                1L,
                "Main",
                "beginDateTime",
                "endDateTime",
                null,
                null,
                null,
                null,
                null,
                null
        );

        InputItem inputItem = mock(InputItem.class);
        given(inputItem.id()).willReturn(1L);
        given(inputItem.loyaltyAccountId()).willReturn("1");
        given(inputItem.beginDateTime()).willReturn("beginDateTime");
        given(inputItem.endDateTime()).willReturn("endDateTime");

        given(itemMapper.enrichItem(expectedBeforeEnriching)).willReturn(expectedAfterEnriching);

        //when
        OutputItem actual = itemMapper.map(inputItem);

        //then
        assertEquals(expectedAfterEnriching, actual);

        then(itemMapper).should().mapAccount(inputItem.loyaltyAccountId());
    }

    @Test
    void should_ReturnNull_when_InputItemIsNull() {
        //when
        //then
        assertNull(itemMapper.map(null));
    }

    @Test
    void should_ThrowMappingException_when_inputItemIsInvalid() {
        InputItem inputItem = mock(InputItem.class);
        given(inputItem.loyaltyAccountId()).willReturn("invalid");

        //when
        //then
        assertThrows(MappingException.class, () -> itemMapper.map(inputItem));
    }
}