package com.ihren.producer.service;

import com.ihren.model.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import com.ihren.model.constant.PersonData;

@Component
public class PersonGeneratorImpl implements PersonGenerator {
    private final Random random = new Random();

    @Override
    public Person generate() {
        return generateRandomPerson();
    }

    private Person generateRandomPerson() {
        return new Person(random(PersonData.FIRST_NAMES), random(PersonData.LAST_NAMES), random.nextInt(12, 100));
    }

    private String random(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
