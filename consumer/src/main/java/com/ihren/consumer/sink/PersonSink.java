package com.ihren.consumer.sink;

import com.ihren.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonSink {
    public void sink(Person person) {
        log.info(person.toString());
    }
}
