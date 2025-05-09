package com.ihren.processor.mapper;

import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        Item expected = new Item(1L, "Main", "beginDateTime", "endDateTime");

        ItemDto itemDto = mock(ItemDto.class);
        given(itemDto.id()).willReturn(1L);
        given(itemDto.loyaltyAccountId()).willReturn("1");
        given(itemDto.beginDateTime()).willReturn("beginDateTime");
        given(itemDto.endDateTime()).willReturn("endDateTime");

        //when
        Item actual = itemMapper.map(itemDto);

        //then
        assertEquals(expected, actual);

        then(itemMapper).should().mapAccount(itemDto.loyaltyAccountId());
    }

    @Test
    void should_ReturnNull_when_ItemDtoIsNull() {
        //when
        //then
        assertNull(itemMapper.map(null));
    }
}