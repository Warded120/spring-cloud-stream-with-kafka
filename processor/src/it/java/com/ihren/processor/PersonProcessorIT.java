package com.ihren.processor;

import com.ihren.model.Person;
import com.ihren.processor.annotation.IntegrationTest;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class PersonProcessorIT {

    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, Person> kafkaConsumer;

    @BeforeEach
    void setUp() {
        kafkaConsumer.subscribe(Collections.singletonList("processed-people"));
    }

    @AfterEach
    void tearDown() {
        kafkaConsumer.close();
    }

    @Test
    void testProcessPerson() {
        //given
        Person person = new Person(1L, "Ivan", "Hrenevych");

        // when
        kafkaTemplate.send("people", person);

        // then
        Try.withResources(() -> kafkaConsumer)
            .of(consumer -> {
                ConsumerRecords<String, Person> consumerRecords = consumer.poll(Duration.ofSeconds(10));
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