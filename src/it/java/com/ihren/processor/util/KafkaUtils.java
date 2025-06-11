package com.ihren.processor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.DeleteRecordsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public final class KafkaUtils {
    //FIXME: is it OK?
    //TODO: move to resourceUtils and inject it as a bean
    private static final ObjectMapper mapper = getObjectMapper();

    //FIXME: is it OK?
    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapperConfig.configure(mapper);
        return mapper;
    }

    public static <K, V> V getRecordValue(KafkaConsumer<K, V> consumer, String topic, Duration timeout) {
        return Try.of(() -> {
                    ConsumerRecord<K, V> record = KafkaTestUtils.getSingleRecord(consumer, topic, timeout);
                    return record.value();
                })
                .recover(ex -> null)
                .get();
    }

    public static <K, V> ConsumerRecord<K, V> getRecord(KafkaConsumer<K, V> consumer, String topic, Duration timeout) {
        return Try.of(() ->
                    KafkaTestUtils.getSingleRecord(consumer, topic, timeout)
                )
                .recover(ex -> null)
                .get();
    }

    public static <K, V> List<V> getRecords(KafkaConsumer<K, V> consumer, String topic, Duration timeout, int minRecords) {
        return Try.of(() -> {
                    ConsumerRecords<K, V> records = KafkaTestUtils.getRecords(consumer, timeout, minRecords);
                    Iterable<ConsumerRecord<K, V>> topicRecords = records.records(topic);
                    return StreamSupport.stream(topicRecords.spliterator(), false)
                            .map(ConsumerRecord::value)
                            .toList();
                })
                .recover(ex -> null)
                .get();
    }

    public static void purgeAllRecords(Admin admin, String topic) {
        Try.run(() -> {
            DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singletonList(topic));
            TopicDescription topicDescription = describeTopicsResult.allTopicNames().get().get(topic);

            Map<TopicPartition, RecordsToDelete> recordsToDeleteMap = new HashMap<>();

            for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
                TopicPartition topicPartition = new TopicPartition(topic, partitionInfo.partition());

                recordsToDeleteMap.put(
                        topicPartition,
                        RecordsToDelete.beforeOffset(-1)
                );
            }

            DeleteRecordsResult deleteRecordsResult = admin.deleteRecords(recordsToDeleteMap);

            deleteRecordsResult.all().get();
        })
        .onFailure(Throwable::printStackTrace);
    }

    //TODO: move to resourceUtils
    public static <T> T read(byte[] bytes, Class<T> clazz) {
        return Try.of(() -> mapper.readValue(bytes, clazz))
                .getOrElseThrow(ex -> new IllegalStateException("Cannot read bytes to " + clazz.getName(), ex));
    }

    public static byte[] write(Object t) {
        return Try.of(() -> mapper.writeValueAsBytes(t))
                .getOrElseThrow(ex -> new IllegalStateException("Cannot write " + t, ex));
    }
}