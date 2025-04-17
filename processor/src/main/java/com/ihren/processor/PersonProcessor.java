package com.ihren.processor;

import com.ihren.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PersonProcessor {
    @Bean
    public Function<Person, Person> processPerson() {
        return person -> person;
    }
}
