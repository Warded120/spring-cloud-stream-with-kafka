package com.ihren.processor;

import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.Currency;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.mapper.TransactionMapper;
import com.ihren.processor.model.Item;
import com.ihren.processor.model.Total;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.util.KafkaUtils;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@IntegrationTest
public class ProcessorIT {
    @Autowired
    private KafkaTemplate<String, TransactionDto> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, Transaction> kafkaConsumer;

    @Autowired
    private Admin admin;

    @MockitoSpyBean
    private TransactionMapper mapper;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.destination}")
    private String topicIn;

    @Value("${spring.cloud.stream.bindings.processTransaction-out-0.destination}")
    private String topicOut;

    @BeforeEach
    public void init() {
        KafkaUtils.purgeAllRecords(admin, topicIn);
        kafkaConsumer.subscribe(Collections.singletonList(topicOut));
    }

    @AfterEach
    public void clean() {
        kafkaConsumer.unsubscribe();
    }

    @Test
    void should_ProcessTransactionDto_when_InputIsValid() {
        //given
        UUID uuid = UUID.randomUUID();
        String endDateTime = "2023-04-10T09:00:00Z";
        Instant operationDateTime = Instant.parse(endDateTime);

        String itemBeginDateTime = "2023-04-10T10:00:00Z";
        String ItemEndDateTime = "2023-04-10T12:00:00Z";
        List<ItemDto> items = List.of(
                new ItemDto(1L, "4", itemBeginDateTime, ItemEndDateTime)
        );

        BigDecimal amount = new BigDecimal("150.00");
        TotalDto total = new TotalDto(amount, "USD");

        TransactionDto transactionDto = new TransactionDto(
                "10.00",
                1L,
                endDateTime,
                items,
                total
        );

        List<Item> expectedItems = List.of(
                new Item(1L, "Total", itemBeginDateTime, ItemEndDateTime)
        );

        Total expectedTotal = new Total(amount, Currency.USD);

        Transaction expectedTransaction = new Transaction(
            uuid,
            Constants.SOFTSERVE,
            null,
            1L,
            operationDateTime,
            expectedItems,
            expectedTotal
        );

        given(mapper.generateTransactionId(transactionDto)).willReturn(uuid);

        kafkaTemplate.send(topicIn, transactionDto);

        //when
        Transaction actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3));

        //then
        assertEquals(expectedTransaction, actual);

        then(mapper).should().generateTransactionId(transactionDto);
    }

    @Test
    void should_LogError_when_TransactionIsInvalid(CapturedOutput output) {
        //given
        List<ItemDto> items = List.of(
                new ItemDto(1L, "4", "2023-04-10T10:00:00Z", "2023-04-10T12:00:00Z"),
                new ItemDto(2L, "2", "2023-04-10T11:00:00Z", "2023-04-10T13:00:00Z")
        );

        TotalDto total = new TotalDto(new BigDecimal("150.00"), "invalid");

        TransactionDto transactionDto = new TransactionDto(
                "10.00",
                12345L,
                "2023-04-10T09:00:00Z",
                items,
                total
        );
        kafkaTemplate.send(topicIn, transactionDto);

        //when
        //then
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3)));

        assertTrue(output.getOut().contains("jakarta.validation.ValidationException"));
    }
}