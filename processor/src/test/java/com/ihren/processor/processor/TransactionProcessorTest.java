package com.ihren.processor.processor;

import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.mapper.ItemMapperImpl;
import com.ihren.processor.mapper.TotalMapperImpl;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.mapper.TransactionMapperImpl;
import com.ihren.processor.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {

    private final TransactionMapper transactionMapper = new TransactionMapperImpl(new ItemMapperImpl(), new TotalMapperImpl());

    @Test
    void applyTest() {
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

        Transaction applied = transactionMapper.map(transactionDto);
        System.out.println(applied);
    }
}