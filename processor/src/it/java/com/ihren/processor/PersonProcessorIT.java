package com.ihren.processor;

import com.ihren.model.Person;
import com.ihren.processor.annotation.IntegrationTest;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class PersonProcessorIT {

    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, Person> kafkaConsumer;

    @Test
    void testProcessPerson() {
        //given
        Person person = new Person(1L, "Ivan", "Hrenevych");

        // when
        kafkaTemplate.send("people", person);
        kafkaConsumer.subscribe(Collections.singletonList("processed-people"));

        // then
        Try.withResources(() -> kafkaConsumer)
            .of(consumer -> {
                ConsumerRecords<String, Person> consumerRecords = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
                assertEquals(1, consumerRecords.count(), "Should have 1 record");
                return consumerRecords.iterator().next();
            })
            .onSuccess(record ->
                assertEquals(person, record.value())
            )
            .onFailure(throwable ->
                Assertions.fail(throwable.getMessage())
            )
            .get();
    }
}