package com.ihren.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.util.KafkaUtils;
import com.ihren.processor.util.TestUtils;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class ProcessorIT {
    private static final Duration TIME_TO_WAIT = Duration.ofSeconds(3);

    @Autowired
    private KafkaTemplate<String, InputTransaction> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, OutputTransaction> kafkaConsumer;

    @Autowired
    private KafkaConsumer<String, InputTransaction> dltKafkaConsumer;

    @Autowired
    private KafkaConsumer<String, String> stringKafkaConsumer;

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    @Qualifier("nonCacheableItemClient")
    private ItemClient nonCacheableItemClient;

    @Autowired
    private Admin admin;

    @Autowired
    private GenericCache<Long, ItemResponse> cache;

    @Autowired
    private ObjectMapper mapper;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.destination}")
    private String topicIn;

    @Value("${spring.cloud.stream.bindings.processTransaction-out-0.destination}")
    private String topicOut;

    @BeforeEach
    public void init() {
        kafkaConsumer.subscribe(Collections.singletonList(topicOut));
        dltKafkaConsumer.subscribe(Collections.singletonList(topicIn.concat(".dlt")));
        stringKafkaConsumer.subscribe(Collections.singletonList(topicIn.concat(".dlt")));
    }

    @AfterEach
    public void clean() {
        KafkaUtils.purgeAllRecords(admin, topicIn);
        KafkaUtils.purgeAllRecords(admin, topicIn.concat(".dlt"));
        KafkaUtils.purgeAllRecords(admin, topicOut);
        kafkaConsumer.unsubscribe();
        dltKafkaConsumer.unsubscribe();
        cache.clearCache();
        WireMock.reset();
    }

    @Test
    void should_Process_when_InputTransactionIsValid() {
        //given
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();
        Message<InputTransaction> message = MessageBuilder
                .withPayload(inputTransaction)
                .setHeader(KafkaHeaders.TOPIC, topicIn)
                .setHeader(Constants.Kafka.Headers.IS_DLT, false)
                .build();

        OutputTransaction expectedTransaction = TestUtils.getExpectedOutputTransaction();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("item-response.json")
                )
        );

        kafkaTemplate.send(message);

        //when
        ConsumerRecord<String, OutputTransaction> record = KafkaUtils.getRecord(kafkaConsumer, topicOut, TIME_TO_WAIT);
        OutputTransaction actual = record.value();

        //then
        assertFalse(KafkaUtils.read(record.headers().lastHeader(Constants.Kafka.Headers.IS_DLT).value(), Boolean.class));
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
    void should_SendToDltWithHeaders_when_InputTransactionIsInvalid() {
        //given
        InputTransaction inputTransaction = TestUtils.getInvalidInputTransaction();
        Message<InputTransaction> message = MessageBuilder
                .withPayload(inputTransaction)
                .setHeader(KafkaHeaders.TOPIC, topicIn)
                .setHeader(Constants.Kafka.Headers.IS_DLT, false)
                .build();

        kafkaTemplate.send(message);

        //when
        ConsumerRecord<String, InputTransaction> record = KafkaUtils.getRecord(dltKafkaConsumer, topicIn.concat(".dlt"), TIME_TO_WAIT);

        //then
        assertNotNull(record);
        assertTrue(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.IS_DLT).value(),
                        Boolean.class
                )
        );
        assertEquals(
                ErrorCode.VALIDATION_EXCEPTION,
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.ERROR_CODE).value(),
                        ErrorCode.class
                )
        );
        assertNotNull(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE).value(),
                        String.class
                )
        );
    }

    @Test
    void should_SendToDltWithHeaders_when_EnrichmentFailed() {
        //given
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();
        Message<InputTransaction> message = MessageBuilder
                .withPayload(inputTransaction)
                .setHeader(KafkaHeaders.TOPIC, topicIn)
                .setHeader(Constants.Kafka.Headers.IS_DLT, false)
                .build();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody("Not Found by id: 1")
                )
        );

        kafkaTemplate.send(message);

        //when
        ConsumerRecord<String, InputTransaction> record = KafkaUtils.getRecord(dltKafkaConsumer, topicIn.concat(".dlt"), TIME_TO_WAIT);

        //then
        assertNotNull(record);
        assertTrue(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.IS_DLT).value(),
                        Boolean.class
                )
        );
        assertEquals(
                ErrorCode.NOT_FOUND_EXCEPTION,
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.ERROR_CODE).value(),
                        ErrorCode.class
                )
        );
        assertNotNull(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE).value(),
                        String.class
                )
        );
    }

    @Test
    void should_CallApiOnce_when_ClientCalledTwice() {
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();
        Message<InputTransaction> message = MessageBuilder
                .withPayload(inputTransaction)
                .setHeader(KafkaHeaders.TOPIC, topicIn)
                .setHeader(Constants.Kafka.Headers.IS_DLT, false)
                .build();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("item-response.json")
                )
        );

        kafkaTemplate.send(message);
        kafkaTemplate.send(message);

        //when
        List<OutputTransaction> actual = KafkaUtils.getRecords(kafkaConsumer, topicOut, TIME_TO_WAIT, 2);

        //then
        assertEquals(2, actual.size());
        then(nonCacheableItemClient).should(times(1)).getById(1L);
    }

    @Test
    void should_SendToDlt_when_DeserializationFailed() {
        //given
        Message<String> message = MessageBuilder
                .withPayload("invalid")
                .setHeader(KafkaHeaders.TOPIC, topicIn)
                .setHeader(Constants.Kafka.Headers.IS_DLT, false)
                .build();

        kafkaTemplate.send(message);

        //when
        //TODO: check if payload is the same ("invalid")
        ConsumerRecord<String, String> record = KafkaUtils.getRecord(stringKafkaConsumer, topicIn.concat(".dlt"), TIME_TO_WAIT);

        //then
        assertNotNull(record);
        assertTrue(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.IS_DLT).value(),
                        Boolean.class
                )
        );
        assertEquals(
                ErrorCode.SERIALIZATION_EXCEPTION,
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.ERROR_CODE).value(),
                        ErrorCode.class
                )
        );
        assertNotNull(
                KafkaUtils.read(
                        record.headers().lastHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE).value(),
                        String.class
                )
        );
    }

    @Test
    @Disabled
    void should_ProcessTransaction_when_ReplayMechanismTriggered() throws Exception {
        //given
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();
        Message<InputTransaction> message = MessageBuilder
                .withPayload(inputTransaction)
                .setHeader(KafkaHeaders.TOPIC, topicIn.concat(".dlt"))
                .setHeader(Constants.Kafka.Headers.IS_DLT, true)
                .build();

        OutputTransaction expectedTransaction = TestUtils.getExpectedOutputTransaction();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("item-response.json")
                )
        );

        kafkaTemplate.send(message);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/replay/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
        ConsumerRecord<String, OutputTransaction> record = KafkaUtils.getRecord(kafkaConsumer, topicOut, TIME_TO_WAIT);

        //then
        OutputTransaction actual = record.value();
        assertFalse(KafkaUtils.read(record.headers().lastHeader(Constants.Kafka.Headers.IS_DLT).value(), Boolean.class));
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

    //TODO: add errorHandlingSerializer logic (the same like errorHandlingDeserializer)
}