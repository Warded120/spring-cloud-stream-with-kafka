package com.ihren.processor.util;

import io.vavr.control.Try;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.time.Duration;
import java.util.Collections;

@UtilityClass
public class KafkaUtils {
    public<K, V> ConsumerRecord<K, V> getRecord(KafkaConsumer<K, V> consumer, String topic, Duration timeout) {
        return Try.withResources(() -> consumer)
            .of(sumer -> {
                sumer.subscribe(Collections.singletonList(topic));
                ConsumerRecord<K, V> record = KafkaTestUtils.getSingleRecord(sumer, topic, timeout);
                sumer.unsubscribe();
                return record;
            })
            .get();
    }
}
