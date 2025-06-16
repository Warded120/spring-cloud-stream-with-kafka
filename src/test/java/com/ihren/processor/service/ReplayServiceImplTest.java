package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import scala.Int;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReplayServiceImplTest {
    public static final String TOPIC_DLT = "test.dlt";

    @InjectMocks
    private ReplayServiceImpl replayService;

    @Mock
    private KafkaConsumer<String, InputTransaction> consumer;

    @Mock
    private StreamBridge streamBridge;

    private final String BINDING_NAME = (String) ReflectionTestUtils.getField(ReplayServiceImpl.class, "BINDING_NAME");
    private final Duration TIME_TO_WAIT = (Duration) ReflectionTestUtils.getField(ReplayServiceImpl.class, "TIME_TO_WAIT");
    private final Integer MINIMUM_ITERATIONS = (Integer) ReflectionTestUtils.getField(ReplayServiceImpl.class, "MINIMUM_ITERATIONS");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(replayService, "topicDlt", TOPIC_DLT);
    }

    @Test
    void should_SendMessage_when_TopicIsNotEmpty() {
        //given
        int expectedCount = 1;
        InputTransaction inputTransaction = mock(InputTransaction.class);
        List<ConsumerRecord<String, InputTransaction>> recordList = List.of(
                new ConsumerRecord<>(TOPIC_DLT, 0, 0, null, inputTransaction)
        );

        Map<TopicPartition, List<ConsumerRecord<String, InputTransaction>>> recordsMap = new HashMap<>();
        recordsMap.put(new TopicPartition(TOPIC_DLT, 0), recordList);
        ConsumerRecords<String, InputTransaction> records = new ConsumerRecords<>(recordsMap);

        ConsumerRecords<String, InputTransaction> emptyRecords = new ConsumerRecords<>(Collections.emptyMap());

        given(consumer.poll(TIME_TO_WAIT))
                .willReturn(records)
                .willReturn(emptyRecords);

        //when
        Integer count = replayService.replayAll();

        //then
        assertEquals(expectedCount, count);

        then(consumer).should().subscribe(Collections.singletonList(TOPIC_DLT));
        then(consumer).should(times(MINIMUM_ITERATIONS)).poll(TIME_TO_WAIT);
        then(streamBridge).should().send(eq(BINDING_NAME), any(Message.class));
        then(consumer).should().unsubscribe();
    }
}