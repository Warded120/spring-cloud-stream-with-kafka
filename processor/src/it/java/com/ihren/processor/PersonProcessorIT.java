package com.ihren.processor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

//TODO: extract these annotation to a custom @IntegrationTest annotation
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = KafkaContextInitializer.class)
@ActiveProfiles("test")
//TODO: add import() for KafkaTemplate and KafkaConsumer to produce and consume messages
public class PersonProcessorIT {
    @Test
    void testProcessPerson() {
    }
}