package com.ihren.consumer.processor;

import com.ihren.consumer.sink.PersonSink;
import com.ihren.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PersonProcessor {
    private final PersonSink personSink;

    public Consumer<Person> processPerson() {
        return personSink::sink;
    }
}
