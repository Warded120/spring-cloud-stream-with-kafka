package com.ihren.processor.converter;

import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemConverterImplTest {
    private final ItemConverter itemConverter = new ItemConverterImpl();

    @Test
    void convert_ShouldReturnOutputItem_WhenInputIsValid() {
        // given
        OutputItem item = new OutputItem(
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
        ItemResponse itemResponse = new ItemResponse(
                new BigDecimal("199.99"),
                "producer",
                "description",
                new BigDecimal("99.99"),
                "UOM",
                "12345678901234"
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

        // when
        OutputItem actual = itemConverter.convert(item, itemResponse);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void convert_ShouldReturnNull_WhenBothInputsAreNull() {
        // when
        OutputItem actual = itemConverter.convert(null, null);

        // then
        assertNull(actual);
    }
}