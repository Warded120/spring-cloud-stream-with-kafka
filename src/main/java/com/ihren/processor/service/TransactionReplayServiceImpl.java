package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
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
import org.springframework.messaging.support.MessageHeaderAccessor;
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
    private static final Duration TIME_TO_WAIT = Duration.ofSeconds(3);
    private static final String BINDING_NAME = "replayTransaction-in-0";

    private final KafkaConsumer<String, InputTransaction> consumer;
    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.kafka.bindings.processTransaction-in-0.consumer.dlq-name}")
    private String topicDlt;

    @PostConstruct
    public void init() {
        consumer.subscribe(Collections.singletonList(topicDlt));
    }

    @PreDestroy
    public void destroy() {
        consumer.unsubscribe();
    }

    //TODO: investigate, is there a more flexible solution?
    public void replay() {
        Stream.generate(() -> consumer.poll(TIME_TO_WAIT))
                .takeWhile(recs -> !recs.isEmpty())
                .flatMap(recs ->
                        StreamSupport.stream(recs.spliterator(), false)
                )
                .forEach(record ->
                        streamBridge.send(BINDING_NAME, messageOf(record))
                );
    }

    private <T> Message<T> messageOf(ConsumerRecord<String, T> record) {
        return MessageBuilder
                .withPayload(record.value())
                //TODO: can I use setHeaders(MessageHeadersAccessor) ?
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
