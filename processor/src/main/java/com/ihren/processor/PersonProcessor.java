package com.ihren.processor;

import com.ihren.model.Person;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PersonProcessor implements Function<Person, Person> {
    @Override
    public Person apply(Person person) {
        System.out.println(person);
        return person;
    }
}
