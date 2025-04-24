package com.ihren.processor;

import org.junit.jupiter.api.Test;

//TODO: extract these annotation to a custom @IntegrationTest annotation
//TODO: add import() for KafkaTemplate and KafkaConsumer to produce and consume messages
@IntegrationTest
public class PersonProcessorIT {
    @Test
    void testProcessPerson() {
    }
}