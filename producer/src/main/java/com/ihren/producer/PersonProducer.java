package com.ihren.producer;

import com.ihren.model.Person;
import com.ihren.producer.service.PersonGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PersonProducer {
    private final PersonGeneratorService personGeneratorService;

    @Bean
    public Supplier<Person> producePerson() {
        return personGeneratorService::generate;
    }
}
