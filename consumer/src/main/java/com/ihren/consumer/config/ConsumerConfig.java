package com.ihren.consumer.config;

import com.ihren.consumer.processor.PersonProcessor;
import com.ihren.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ConsumerConfig {
    @Bean
    public Consumer<Person> consumePerson(PersonProcessor personProcessor) {
        return personProcessor;
    }
}
