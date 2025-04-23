package com.ihren.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.misc.ObjectMapperFactory;
import com.ihren.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@ActiveProfiles("test")
public class PersonProcessorIT {

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private OutputDestination outputDestination;

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Test
    void testProcessPerson() throws Exception {
        Person inputPerson = new Person(1, "John", "Doe");

        inputDestination.send(new GenericMessage<>(objectMapper.writeValueAsString(inputPerson)));

        var outputMessage = outputDestination.receive(5000);
        assertNotNull(outputMessage);

        Person outputPerson = objectMapper.readValue(outputMessage.getPayload(), Person.class);
        assertEquals(inputPerson, outputPerson);
    }
}