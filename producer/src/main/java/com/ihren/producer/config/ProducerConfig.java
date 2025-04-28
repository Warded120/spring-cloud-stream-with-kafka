package com.ihren.producer.config;

import com.ihren.model.Person;
import com.ihren.producer.processor.PersonProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class ProducerConfig {
    @Bean
    public Supplier<Person> producePerson(PersonProcessor personProcessor) {
        return personProcessor;
    }
}
