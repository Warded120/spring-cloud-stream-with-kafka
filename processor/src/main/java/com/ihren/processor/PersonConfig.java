package com.ihren.processor;

import com.ihren.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class PersonConfig {
    @Bean
    public Function<Person, Person> processPerson(PersonProcessor processor) {
        return processor;
    }


}
