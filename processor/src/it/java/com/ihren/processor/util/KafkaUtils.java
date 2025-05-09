package com.ihren.processor.util;

import io.vavr.control.Try;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.time.Duration;

@UtilityClass
public class KafkaUtils {
    public<K, V> ConsumerRecord<K, V> getRecord(KafkaConsumer<K, V> consumer, String topic, Duration timeout) {
        return Try.of(() -> KafkaTestUtils.getSingleRecord(consumer, topic, timeout))
                .recover(ex -> null)
                .get();
    }

    public <K, V> boolean hasRecord(KafkaConsumer<K, V> consumer, String topic, Duration timeout) {
        return Try.of(() -> getRecord(consumer, topic, timeout).value() != null)
                .recover(ex -> false)
                .get();
    }
}