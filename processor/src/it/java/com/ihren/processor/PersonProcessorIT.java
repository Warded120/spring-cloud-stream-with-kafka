package com.ihren.processor;

import com.ihren.processor.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
public class PersonProcessorIT {

    @Value("${spring.cloud.stream.bindings.processPerson-in-0.destination}")
    private String topicIn;

    @Value("${spring.cloud.stream.bindings.processPerson-out-0.destination}")
    private String topicOut;

//    @Autowired
//    private KafkaTemplate<String, Transaction> kafkaTemplate;

//    @Autowired
//    private KafkaConsumer<String, Transaction> kafkaConsumer;

    @Test
    void testProcessPerson() {
    }
}