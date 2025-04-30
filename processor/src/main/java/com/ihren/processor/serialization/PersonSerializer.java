package com.ihren.processor.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import com.ihren.processor.model.Person;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PersonSerializer implements Serializer<Person> {

    private final ObjectMapper objectMapper;

    public PersonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, Person person) {
        return Try.of(() -> objectMapper.writeValueAsBytes(person))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
