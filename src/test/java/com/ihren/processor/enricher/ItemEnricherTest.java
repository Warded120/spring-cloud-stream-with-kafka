package com.ihren.processor.enricher;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemEnricherTest {
    @Spy
    @InjectMocks
    private final ItemEnricher enricher = new ItemEnricherImpl();

    @Mock
    private ClientService clientService;

    @Test
    void should_CallEnrichWithOutputItemAndItemResponse_when_EverythingIsOK() {
        //given
        ItemResponse itemResponse = mock();

        OutputItem outputItem = mock();
        given(outputItem.getId()).willReturn(1L);

        given(clientService.getByItemId(1L)).willReturn(itemResponse);

        //when
        enricher.enrich(outputItem);

        //then
        then(enricher).should().enrich(outputItem, itemResponse);
    }

    @Test
    void should_EnrichTargetItem() {
        //given
        ItemResponse itemResponse = mock();
        given(itemResponse.price()).willReturn(new BigDecimal("199.99"));
        given(itemResponse.producer()).willReturn("producer");
        given(itemResponse.description()).willReturn("description");
        given(itemResponse.VATRate()).willReturn(new BigDecimal("99.99"));
        given(itemResponse.UOM()).willReturn("UOM");
        given(itemResponse.barCode()).willReturn("12345678901234");

        OutputItem targetItem = new OutputItem(
                1L,
                "Main",
                "2025-01-01T10:00:00Z",
                "2025-01-01T12:00:00Z",
                null,
                null,
                null,
                null,
                null,
                null
        );

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

        //when
        enricher.enrich(targetItem, itemResponse);

        //then
        assertEquals(expected, targetItem);
    }
}