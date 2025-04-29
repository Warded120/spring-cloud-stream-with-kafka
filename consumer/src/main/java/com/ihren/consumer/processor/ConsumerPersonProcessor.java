package com.ihren.consumer.processor;

import com.ihren.consumer.sink.PersonSink;
import com.ihren.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ConsumerPersonProcessor implements Consumer<Person> {
    private final PersonSink personSink;

    @Override
    public void accept(Person person) {
        personSink.sink(person);
    }
}
