package com.ihren.processor.util;

import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.model.input.InputTotal;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.output.OutputTransaction;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@UtilityClass
public class TestUtils {
    public static InputTransaction getValidInputTransaction() {
        String endDateTime = "2023-04-10T09:00:00Z";

        List<InputItem> inputItems = List.of(
                new InputItem(
                        1L,
                        "4",
                        "2023-04-10T10:00:00Z",
                        "2023-04-10T12:00:00Z"
                )
        );

        InputTotal inputTotal = new InputTotal(
                new BigDecimal("150.00"),
                "USD"
        );

        return new InputTransaction(
                "10.00",
                1L,
                endDateTime,
                inputItems,
                inputTotal
        );
    }

    public static InputTransaction getInvalidInputTransaction() {
        List<InputItem> inputItems = List.of(
                new InputItem(
                        1L,
                        "5",
                        "2023-04-10T10:00:00Z",
                        "2023-04-10T12:00:00Z"
                ),
                new InputItem(
                        2L,
                        "2",
                        "2023-04-10T11:00:00Z",
                        "2023-04-10T13:00:00Z"
                )
        );

        InputTotal inputTotal = new InputTotal(
                new BigDecimal("150.00"),
                "invalid"
        );

        return new InputTransaction(
                "10.00",
                12345L,
                "2023-04-10T09:00:00Z",
                inputItems,
                inputTotal
        );
    }

    public static OutputTransaction getExpectedOutputTransaction() {
        Long id = 1L;
        String total = "Total";
        String endDateTime = "2023-04-10T09:00:00Z";
        Instant operationDateTime = Instant.parse(endDateTime);
        String itemBeginDateTime = "2023-04-10T10:00:00Z";
        String itemEndDateTime = "2023-04-10T12:00:00Z";
        BigDecimal amount = new BigDecimal("150.00");
        BigDecimal price = new BigDecimal("150.00");
        String producer = "producer";
        String description = "description";
        BigDecimal VATRate = new BigDecimal("99.99");
        String UOM = "UOM";
        String BarCode = "12345678901234";

        List<OutputItem> expectedItems = List.of(
                new OutputItem(
                        id,
                        total,
                        itemBeginDateTime,
                        itemEndDateTime,
                        price,
                        producer,
                        description,
                        VATRate,
                        UOM,
                        BarCode
                )
        );

        OutputTotal expectedTotal = new OutputTotal(amount, CurrencyCode.USD);

        return new OutputTransaction(
                null,
                TransactionMapper.SOFTSERVE,
                null,
                1L,
                operationDateTime,
                expectedItems,
                expectedTotal
        );
    }
}
