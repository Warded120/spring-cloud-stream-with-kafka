package com.ihren.misc.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.misc.mapper.ObjectMapperConfig;
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
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configureObjectMapper(objectMapper);
    }
    @Override
    public Person deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, Person.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}
