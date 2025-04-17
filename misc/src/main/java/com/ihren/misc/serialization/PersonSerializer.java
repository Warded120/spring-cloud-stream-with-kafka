package com.ihren.misc.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.misc.ObjectMapperFactory;
import com.ihren.model.Person;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
public class PersonSerializer implements Serializer<Person> {

    private ObjectMapper objectMapper;

    public PersonSerializer() {
        this.objectMapper = ObjectMapperFactory.create();
    }

    @Override
    public byte[] serialize(String s, Person person) {
        return Try.of(() -> objectMapper.writeValueAsBytes(person))
                .getOrElseThrow(e -> new RuntimeException("failed to serialize person", e));
    }
}
