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
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.kafka.core.KafkaTemplate;
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
    @Autowired
    private KafkaTemplate<String, InputTransaction> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, OutputTransaction> kafkaConsumer;

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

        String body = """
                    {
                      "price": 150.00,
                      "producer": "producer",
                      "description": "description",
                      "VATRate": 99.99,
                      "UOM": "UOM",
                      "barCode": "12345678901234"
                    }
                """;
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(body)
                )
        );

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        OutputTransaction actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3));

        //then
        assertAll(
                () -> assertNotNull(actual.getTransactionId()),
                () -> assertEquals(expectedTransaction.getSource(), actual.getSource()),
                () -> assertNull(actual.getDiscount()),
                () -> assertEquals(expectedTransaction.getSequenceNumber(), actual.getSequenceNumber()),
                () -> assertEquals(expectedTransaction.getOperationDateTime(), actual.getOperationDateTime()),
                () -> assertArrayEquals(expectedTransaction.getItems().toArray(), actual.getItems().toArray()),
                () -> assertEquals(expectedTransaction.getTotal(), actual.getTotal())
        );
    }

    @Test
    void should_LogError_when_InputTransactionIsInvalid(CapturedOutput output) {
        //given
        InputTransaction inputTransaction = TestUtils.getInvalidInputTransaction();

        kafkaTemplate.send(topicIn, inputTransaction);

        //when
        //then
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3)));
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
        assertFalse(KafkaUtils.hasRecord(kafkaConsumer, topicOut, Duration.ofSeconds(3)));
        assertTrue(output.getOut().contains("com.ihren.processor.exception.NotFoundException"));
    }

    @Test
    void should_CallApiOnce_when_ClientCalledTwice() {
        InputTransaction inputTransaction1 = TestUtils.getValidInputTransaction();
        InputTransaction inputTransaction2 = TestUtils.getValidInputTransaction();

        String body = """
                    {
                      "price": 150.00,
                      "producer": "producer",
                      "description": "description",
                      "VATRate": 99.99,
                      "UOM": "UOM",
                      "barCode": "12345678901234"
                    }
                """;
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(body)
                )
        );

        kafkaTemplate.send(topicIn, inputTransaction1);
        kafkaTemplate.send(topicIn, inputTransaction2);

        //when
        List<OutputTransaction> actual = KafkaUtils.getRecords(kafkaConsumer, topicOut, Duration.ofSeconds(3), 2);

        //then
        assertEquals(2, actual.size());
        then(nonCacheableItemClient).should(times(1)).getById(1L);
    }
}