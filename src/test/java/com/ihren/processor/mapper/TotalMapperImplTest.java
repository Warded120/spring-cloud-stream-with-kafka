package com.ihren.processor.mapper;

import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.input.InputTotal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TotalMapperImplTest {
    @Spy
    private final TotalMapper totalMapper = new TotalMapperImpl();

    @Test
    void should_ReturnOutputTotal_when_InputTotalIsValid() {
        //given
        OutputTotal expected = new OutputTotal(BigDecimal.valueOf(360L), CurrencyCode.USD);

        InputTotal inputTotal = mock(InputTotal.class);
        given(inputTotal.amount()).willReturn(BigDecimal.valueOf(360L));
        given(inputTotal.currency()).willReturn("USD");

        //when
        OutputTotal actual = totalMapper.map(inputTotal);

        //then
        assertEquals(expected, actual);
        then(totalMapper).should().mapCurrency("USD");
    }

    @Test
    void should_ReturnNull_when_InputTotalIsNull() {
        //when
        //then
        assertNull(totalMapper.map(null));
    }
}