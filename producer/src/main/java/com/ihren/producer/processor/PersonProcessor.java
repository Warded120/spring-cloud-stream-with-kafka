package com.ihren.producer.processor;

import com.ihren.model.Person;
import com.ihren.producer.service.PersonGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PersonProcessor implements Supplier<Person> {
    private final PersonGenerator personGenerator;

    @Override
    public Person get() {
        return personGenerator.generate();
    }
}
