package com.ihren.processor.mapper;

import com.ihren.processor.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    public Person map(Person person) {
        return person;
    }
}
