package com.ihren.processor.enricher;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.converter.ItemConverter;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemEnricherTest {
    @InjectMocks
    private ItemEnricher enricher;

    @Mock
    ClientService clientService;

    @Mock
    ItemConverter itemConverter;

    @Test
    void should_Enrich_when_EverythingIsOK() {
        //given
        OutputItem outputItem = mock();
        given(outputItem.id()).willReturn(1L);

        ItemResponse itemResponse = mock();

        OutputItem expected = new OutputItem(
                1L,
                "Main",
                "2025-01-01T10:00:00Z",
                "2025-01-01T12:00:00Z",
                new BigDecimal("199.99"),
                "producer",
                "description",
                new BigDecimal("99.99"),
                "UOM",
                "12345678901234"
        );

        given(clientService.getByItemId(1L)).willReturn(itemResponse);
        given(itemConverter.convert(outputItem, itemResponse)).willReturn(expected);

        //when
        OutputItem actual = enricher.enrich(outputItem);

        //then
        assertEquals(expected, actual);
    }
}