package com.ihren.processor;

import com.ihren.model.Person;
import com.ihren.processor.annotation.IntegrationTest;
import com.ihren.processor.util.KafkaUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class PersonProcessorIT {

    @Value("${test.topic.in}")
    private String topicIn;

    @Value("${test.topic.out}")
    private String topicOut;

    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    @Autowired
    private KafkaConsumer<String, Person> kafkaConsumer;

    @Test
    void testProcessPerson() {
        //given
        Person person = new Person(1L, "Ivan", "Hrenevych");

        // when
        kafkaTemplate.send(topicIn, person);

        // then
        Person actual = KafkaUtils.getRecord(kafkaConsumer, topicOut, Duration.ofSeconds(10)).value();
        assertEquals(person, actual);
    }
}