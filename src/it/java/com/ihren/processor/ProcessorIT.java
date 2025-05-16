package com.ihren.processor;

import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.CurrencyCode;
import com.ihren.processor.model.output.OutputItem;
import com.ihren.processor.model.output.OutputTotal;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputItem;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.model.input.InputTotal;
import com.ihren.processor.util.KafkaUtils;
import com.ihren.processor.util.TestUtils;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.kafka.core.KafkaTemplate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class ProcessorIT {
    @Autowired
    private KafkaTemplate<String, InputTransaction> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, OutputTransaction> kafkaConsumer;

    @Autowired
    private Admin admin;

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
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();

        OutputTransaction expectedTransaction = TestUtils.getExpectedOutputTransaction();

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        OutputTransaction actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3));

        //then
        assertAll(
                () -> assertNotNull(actual.transactionId()),
                () -> assertEquals(expectedTransaction.source(), actual.source()),
                () -> assertNull(actual.discount()),
                () -> assertEquals(expectedTransaction.sequenceNumber(), actual.sequenceNumber()),
                () -> assertEquals(expectedTransaction.operationDateTime(), actual.operationDateTime()),
                () -> assertArrayEquals(expectedTransaction.items().toArray(), actual.items().toArray()),
                () -> assertEquals(expectedTransaction.total(), actual.total())
        );
    }

    @Test
    void should_LogError_when_TransactionIsInvalid(CapturedOutput output) {
        //given
        InputTransaction inputTransaction = TestUtils.getInvalidInputTransaction();

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        //then
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3)));
        assertTrue(output.getOut().contains("jakarta.validation.ValidationException"));
    }
}