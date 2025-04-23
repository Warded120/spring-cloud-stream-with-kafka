package com.ihren.misc.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ihren.misc.mapper.ObjectMapperFactory;
import com.ihren.misc.serialization.exception.SerializationException;
import com.ihren.model.Person;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

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
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
