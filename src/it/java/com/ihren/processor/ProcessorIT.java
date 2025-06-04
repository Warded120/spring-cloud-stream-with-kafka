package com.ihren.processor;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.model.output.OutputTransaction;
import com.ihren.processor.model.input.InputTransaction;
import com.ihren.processor.util.KafkaUtils;
import com.ihren.processor.util.TestUtils;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
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

@IntegrationTest
public class ProcessorIT {
    private static final Duration TIME_TO_WAIT = Duration.ofMillis(1500);

    @Autowired
    private KafkaTemplate<String, InputTransaction> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateString;

    @Autowired
    private KafkaConsumer<String, OutputTransaction> kafkaConsumer;

    @Autowired
    private KafkaConsumer<String, String> kafkaConsumerS;


    @MockitoSpyBean
    @Qualifier("nonCacheableItemClient")
    private ItemClient nonCacheableItemClient;

    @Autowired
    private Admin admin;

    @Autowired
    private GenericCache<Long, ItemResponse> cache;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.destination}")
    private String topicIn;

    @Value("${spring.cloud.stream.bindings.processTransaction-out-0.destination}")
    private String topicOut;

    @BeforeEach
    public void init() {
        kafkaConsumer.subscribe(Collections.singletonList(topicOut));
        kafkaConsumerS.subscribe(Collections.singletonList("test.dlt"));
    }

    @AfterEach
    public void clean() {
        KafkaUtils.purgeAllRecords(admin, topicIn);
        KafkaUtils.purgeAllRecords(admin, topicOut);
        kafkaConsumer.unsubscribe();
        cache.clearCache();
        WireMock.reset();
    }

    @Test
    void should_Process_when_InputTransactionIsValid() {
        //given
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();

        OutputTransaction expectedTransaction = TestUtils.getExpectedOutputTransaction();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("item-response.json")
                )
        );

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        OutputTransaction actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, TIME_TO_WAIT);

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
    void should_LogError_when_InputTransactionIsInvalid(CapturedOutput output) {
        //given
        InputTransaction inputTransaction = TestUtils.getInvalidInputTransaction();

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        //then
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, TIME_TO_WAIT));
        assertTrue(output.getOut().contains("jakarta.validation.ValidationException"));
    }

    @Test
    void should_LogError_when_EnrichmentFailed(CapturedOutput output) {
        //given
        InputTransaction inputTransaction = TestUtils.getValidInputTransaction();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody("Not Found by id: 1")
                )
        );

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        //then
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, TIME_TO_WAIT));
        assertTrue(output.getOut().contains("com.ihren.processor.exception.NotFoundException"));
    }

    @Test
    void should_CallApiOnce_when_ClientCalledTwice() {
        InputTransaction inputTransaction1 = TestUtils.getValidInputTransaction();
        InputTransaction inputTransaction2 = TestUtils.getValidInputTransaction();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("item-response.json")
                )
        );

        kafkaTemplate.send(topicIn, inputTransaction1);
        kafkaTemplate.send(topicIn, inputTransaction2);

        //when
        List<OutputTransaction> actual = KafkaUtils.getRecords(kafkaConsumer, topicOut, TIME_TO_WAIT, 2);

        //then
        assertEquals(2, actual.size());
        then(nonCacheableItemClient).should(times(1)).getById(1L);
    }

    //TODO: remove later
    @Test
    void should_handleDeserializationException() {
        Message<String> message = MessageBuilder
                .withPayload("invalid")
                .setHeader(KafkaHeaders.TOPIC, "test")
                .setHeader(KafkaHeaders.PARTITION, null)
                .setHeader(KafkaHeaders.KEY, null)
                .setHeader("isReply", false)
                .build();

        kafkaTemplate.send(message);

        ConsumerRecord<String, String> record = KafkaUtils.getRecordd(kafkaConsumerS, "test.dlt", Duration.ofSeconds(10));
        assertNotNull(record);
        assertNotNull(record.headers().lastHeader("exception"));
        System.out.println("isReply: " + new String(record.headers().lastHeader("isReply").value()));
        assertTrue(Boolean.valueOf(new String(record.headers().lastHeader("isReply").value())));
    }
}