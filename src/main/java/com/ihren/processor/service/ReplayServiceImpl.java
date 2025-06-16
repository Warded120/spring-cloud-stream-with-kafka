package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ReplayServiceImpl implements ReplayService {
    private static final int MINIMUM_ITERATIONS = 10;
    private static final Duration TIME_TO_WAIT = Duration.ofMillis(500);
    private static final String BINDING_NAME = "reprocessTransaction-in-0";

    private final KafkaConsumer<String, InputTransaction> consumer;
    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.kafka.bindings.processTransaction-in-0.consumer.dlq-name}")
    private String topicDlt;

    public Integer replayAll() {
        consumer.subscribe(Collections.singletonList(topicDlt));
        AtomicInteger iteration = new AtomicInteger(0);
        return Try.of(() ->
                //TODO: optimize, it's not a good solution
                        Stream.generate(() -> {
                                    iteration.incrementAndGet();
                                    return consumer.poll(TIME_TO_WAIT);
                                })
                                .takeWhile(recs -> recs.count() > 0 || iteration.get() < MINIMUM_ITERATIONS)
                                .peek(recs ->
                                        recs.forEach(record ->
                                                streamBridge.send(BINDING_NAME, messageOf(record))
                                        )
                                )
                                .mapToInt(ConsumerRecords::count)
                                .sum())
                .andFinally(consumer::unsubscribe)
                .get();
    }

    private <T> Message<T> messageOf(ConsumerRecord<String, T> record) {
        return MessageBuilder
                .withPayload(record.value())
                .copyHeaders(mapOf(record.headers()))
                .build();
    }

    private Map<String, Object> mapOf(Headers headers) {
        return Optional.ofNullable(headers)
                .map(hs ->
                        StreamSupport.stream(hs.spliterator(), false)
                                .collect(Collectors.toMap(
                                                Header::key,
                                                Header::value,
                                                (oldValue, newValue) -> newValue,
                                                () -> new HashMap<String, Object>()
                                        )
                                )
                )
                .orElseGet(HashMap::new);
    }
}
