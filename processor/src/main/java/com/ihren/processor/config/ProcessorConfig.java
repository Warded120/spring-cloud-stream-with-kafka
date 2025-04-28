package com.ihren.processor.config;

import com.ihren.model.Person;
import com.ihren.processor.processor.PersonProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ProcessorConfig {
    @Bean
    public Function<Person, Person> processPerson(PersonProcessor personProcessor) {
        return personProcessor;
    }
}
