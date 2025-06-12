package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReplayService {
    private final KafkaConsumer<String, InputTransaction> consumer;
    private final StreamBridge streamBridge;

    public Integer replayAll() {
        //TODO: retrieve from config
        consumer.subscribe(Collections.singletonList("test.dlt"));

        ConsumerRecords<String, InputTransaction> records = consumer.poll(Duration.ofSeconds(3));
        return Stream.iterate(records, (recs) -> !recs.isEmpty(), recs -> consumer.poll(Duration.ofSeconds(1)))
                //TODO: is it okay to use peek?
                .peek(rs ->
                    rs.forEach(r ->
                            streamBridge.send("reprocessTransaction-in-0", r.value())
                    )
                )
                .map(ConsumerRecords::count)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
