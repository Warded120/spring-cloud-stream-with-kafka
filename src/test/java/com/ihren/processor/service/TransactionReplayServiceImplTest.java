package com.ihren.processor.service;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.model.input.InputTransaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TransactionReplayServiceImplTest {
    private static final Duration TIME_TO_WAIT = Duration.ofSeconds(5);
    private static final String BINDING_NAME = "replayTransaction-in-0";

    @InjectMocks
    private TransactionReplayServiceImpl replayService;

    @Mock
    private KafkaConsumer<String, InputTransaction> consumer;

    @Mock
    private StreamBridge streamBridge;

    public final String DLT = "test.dlt";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(replayService, "topicDlt", DLT);
    }

    @Test
    void should_subscribeToTopic() {
        //when
        replayService.init();
        //then
        then(consumer).should().subscribe(Collections.singletonList(DLT));
    }

    @Test
    void should_unsubscribe() {
        //when
        replayService.destroy();
        //then
        then(consumer).should().unsubscribe();
    }

    @Test
    void should_SendMessage_when_TopicIsNotEmpty() {
        //given
        String originalTopic = "test";
        InputTransaction inputTransaction = mock(InputTransaction.class);
        Headers headers = new RecordHeaders(
                List.of(
                        new RecordHeader(Constants.Kafka.Headers.ORIGINAL_TOPIC, originalTopic.getBytes())
                )
        );
        ConsumerRecord<String, InputTransaction> record = new ConsumerRecord<>(
                DLT,
                0,
                0,
                System.currentTimeMillis(),
                TimestampType.CREATE_TIME,
                0L,
                0,
                0,
                null,
                inputTransaction,
                headers
        );

        List<ConsumerRecord<String, InputTransaction>> recordList = List.of(
                record
        );

        Map<TopicPartition, List<ConsumerRecord<String, InputTransaction>>> recordsMap = new HashMap<>();
        recordsMap.put(new TopicPartition(DLT, 0), recordList);
        ConsumerRecords<String, InputTransaction> records = new ConsumerRecords<>(recordsMap);

        ConsumerRecords<String, InputTransaction> emptyRecords = new ConsumerRecords<>(Collections.emptyMap());

        given(consumer.poll(TIME_TO_WAIT))
                .willReturn(records)
                .willReturn(emptyRecords);

        //when
        replayService.replay();

        //then
        then(consumer).should(atLeastOnce()).poll(TIME_TO_WAIT);
        then(streamBridge).should().send(eq(originalTopic), any(Message.class));
    }
}