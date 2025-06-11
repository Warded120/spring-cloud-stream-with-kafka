package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;
import java.util.stream.Stream;

@Service
public class ReplayService {
    private final KafkaConsumer<String, InputTransaction> consumer;
    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.bindings.processTransaction-in-0.destination}")
    private String topicIn;

    public ReplayService(
            KafkaConsumer<String, InputTransaction> consumer,
            StreamBridge streamBridge
    ) {
        this.consumer = consumer;
        this.streamBridge = streamBridge;
    }

    public Integer replayAll() {
        consumer.subscribe(Collections.singletonList(topicIn.concat(".dlt")));

        ConsumerRecords<String, InputTransaction> records = consumer.poll(Duration.ofSeconds(1));
        return Stream.iterate(records, ConsumerRecords::isEmpty, recs -> consumer.poll(Duration.ofSeconds(1)))
                //TODO: is it okay to use peek?
                .peek(rs -> streamBridge.send(topicIn.concat(".replay"), rs))
                .map(ConsumerRecords::count)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
