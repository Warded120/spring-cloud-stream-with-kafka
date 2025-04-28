package com.ihren.processor;

import com.ihren.model.Person;
import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.config.KafkaConsumerConfig;
import com.ihren.processor.config.KafkaTemplateConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@IntegrationTest
@Import({KafkaTemplateConfig.class, KafkaConsumerConfig.class})
public class PersonProcessorIT {

    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    @Autowired
    KafkaConsumer<String, Person> kafkaConsumer;

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
        Person person = new Person(1L, "Ivan", "Hrenevych");

        // when
        kafkaTemplate.send("people", person);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                () -> {
                    ConsumerRecords<String, Person> records = kafkaConsumer.poll(Duration.ofMillis(500));
                    if (!records.isEmpty()) {
                        Person processedPerson = records.iterator().next().value();
                        assertEquals(person, processedPerson);
                    } else {
                        fail("person not processed");
                    }
                }
        );
    }
}