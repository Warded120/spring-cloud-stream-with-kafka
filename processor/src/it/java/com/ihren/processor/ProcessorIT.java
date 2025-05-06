package com.ihren.processor;

import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.dto.TotalDto;
import com.ihren.processor.dto.TransactionDto;
import com.ihren.processor.model.Transaction;
import com.ihren.processor.util.KafkaUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class ProcessorIT {
    @Autowired
    private KafkaTemplate<String, TransactionDto> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, Transaction> kafkaConsumer;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.destination}")
    private String topicIn;

    @Value("${spring.cloud.stream.bindings.processTransaction-out-0.destination}")
    private String topicOut;

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
                "10.00",
                12345L,
                "2023-04-10T09:00:00Z",
                items,
                total
        );
        kafkaTemplate.send(topicIn, transactionDto);

        //when
        Transaction actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, Duration.ofSeconds(10)).value();

        //then
        assertNotNull(actual);
        System.out.println(actual);
    }
}