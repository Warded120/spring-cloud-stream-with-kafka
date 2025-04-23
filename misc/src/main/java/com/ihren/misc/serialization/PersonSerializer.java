package com.ihren.misc.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ihren.misc.mapper.ObjectMapperFactory;
import com.ihren.misc.serialization.exception.SerializationException;
import com.ihren.model.Person;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonSerializer implements Serializer<Person> {

    private final ObjectMapper objectMapper;

    public PersonSerializer() {
        this.objectMapper = ObjectMapperFactory.create();
    }

    @Override
    public byte[] serialize(String s, Person person) {
        return Try.of(() -> objectMapper.writeValueAsBytes(person))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        extracted();
    }

    private void extracted() {
        objectMapper
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
