package com.ihren.processor;

import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.processor.TransactionProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

@IntegrationTest
public class ProcessorIT {
//    @Autowired
//    private KafkaTemplate<String, Transaction> kafkaTemplate;

//    @Autowired
//    private KafkaConsumer<String, Transaction> kafkaConsumer;

    @Autowired
    private TransactionProcessor transactionProcessor;

    //TODO: setup application-test.yml
    @Test
    void applyTest() {
        //given
        List<ItemDto> items = List.of(
                new ItemDto(1L, "1", "2023-04-10T10:00:00Z", "2023-04-10T12:00:00Z"),
                new ItemDto(2L, "2", "2023-04-10T11:00:00Z", "2023-04-10T13:00:00Z")
        );

        TotalDto total = new TotalDto(new BigDecimal("150.00"), "USD");

        TransactionDto transactionDto = new TransactionDto(
                "10.0",
                12345L,
                "2023-04-10T09:00:00Z",
                items,
                total
        );

        Transaction applied = transactionProcessor.apply(transactionDto);
        System.out.println(applied);
    }
}