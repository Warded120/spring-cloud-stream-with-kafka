package com.ihren.processor.mapper;

import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.mapper.exception.MappingException;
import com.ihren.processor.model.output.OutputItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemMapperImplTest {
    @Spy
    private final ItemMapper itemMapper = new ItemMapperImpl();

    @Test
    void should_ReturnItem_when_ItemDtoIsValid() {
        //given
        OutputItem expected = new OutputItem(1L, "Main", "beginDateTime", "endDateTime");

        InputItem inputItem = mock(InputItem.class);
        given(inputItem.id()).willReturn(1L);
        given(inputItem.loyaltyAccountId()).willReturn("1");
        given(inputItem.beginDateTime()).willReturn("beginDateTime");
        given(inputItem.endDateTime()).willReturn("endDateTime");

        //when
        OutputItem actual = itemMapper.map(inputItem);

        //then
        assertEquals(expected, actual);

        then(itemMapper).should().mapAccount(inputItem.loyaltyAccountId());
    }

    @Test
    void should_ReturnNull_when_ItemDtoIsNull() {
        //when
        //then
        assertNull(itemMapper.map(null));
    }

    @Test
    void should_ThrowMappingException_when_inputIsInvalid() {
        InputItem inputItem = mock(InputItem.class);
        given(inputItem.loyaltyAccountId()).willReturn("invalid");

        //when
        //then
        //TODO: why fails
        assertThrows(MappingException.class, () -> itemMapper.map(inputItem));
    }
}