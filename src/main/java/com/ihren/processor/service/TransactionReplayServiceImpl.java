package com.ihren.processor.service;

import com.ihren.processor.constant.Constants;
import com.ihren.processor.model.input.InputTransaction;
import io.vavr.control.Try;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TransactionReplayServiceImpl implements TransactionReplayService {
    private static final Duration TIME_TO_WAIT = Duration.ofSeconds(5);
    private static final String BINDING_NAME = "replayTransaction-in-0";

    private final KafkaConsumer<String, InputTransaction> consumer;
    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.kafka.bindings.processTransaction-in-0.consumer.dlq-name}")
    private String dlt;

    @PostConstruct
    public void init() {
        consumer.subscribe(Collections.singletonList(dlt));
    }

    @PreDestroy
    public void destroy() {
        consumer.unsubscribe();
    }

    public void replay() {
        Stream.generate(() -> consumer.poll(TIME_TO_WAIT))
                .takeWhile(recs -> !recs.isEmpty())
                .flatMap(recs -> StreamSupport.stream(recs.spliterator(), false))
                .toList()
                .forEach(record ->
                        streamBridge.send(getDestination(record), messageOf(record))
                );
    }

    private <T> String getDestination(ConsumerRecord<String, T> record) {
        return Try.of(() ->
                        new String(record.headers().lastHeader(Constants.Kafka.Headers.ORIGINAL_TOPIC).value())
                )
                .getOrElse(BINDING_NAME);
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
