package com.ihren.processor.processor;

import com.ihren.model.Person;
import com.ihren.processor.sink.PersonSink;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PersonProcessor {
    private final PersonSink personSink;

    public Function<Person, Person> processPerson() {
        return personSink::sink;
    }
}
