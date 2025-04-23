package com.ihren.processor.sink;

import com.ihren.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonSink {
    public Person sink(Person person) {
        return person;
    }
}
