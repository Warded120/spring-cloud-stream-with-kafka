package com.ihren.processor.processor;

import com.ihren.processor.model.Person;
import com.ihren.processor.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PersonProcessor implements Function<Person, Person> {
    private final PersonMapper personMapper;

    @Override
    public Person apply(Person person) {
        return personMapper.map(person);
    }
}
