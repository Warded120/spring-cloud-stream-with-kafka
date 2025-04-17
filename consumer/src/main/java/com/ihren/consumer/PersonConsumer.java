package com.ihren.consumer;

import com.ihren.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Component
public class PersonConsumer {
    @Bean
    public Consumer<Person> consumePerson() {
        return System.out::println;
    }
}
