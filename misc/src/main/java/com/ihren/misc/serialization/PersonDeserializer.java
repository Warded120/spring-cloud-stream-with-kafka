package com.ihren.misc.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.misc.ObjectMapperFactory;
import com.ihren.model.Person;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonDeserializer implements Deserializer<Person> {

    private final ObjectMapper objectMapper;

    public PersonDeserializer() {
        this.objectMapper = ObjectMapperFactory.create();
    }

    @Override
    public Person deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, Person.class))
                .getOrElseThrow(e -> new RuntimeException("failed to deserialize person", e));
    }
}
