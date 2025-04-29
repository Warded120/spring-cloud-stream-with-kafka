package com.ihren.processor.mapper;

import com.ihren.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    public Person map(Person person) {
        return person;
    }
}
