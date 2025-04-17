package com.ihren.producer.service;

import com.ihren.model.Person;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import com.ihren.model.constant.PersonData;

@Service
public class PersonGeneratorServiceImpl implements PersonGeneratorService {
    private static int id = 0;
    private static final Random random = new Random();

    @Override
    public Person generate() {
        return generateRandomPerson();
    }

    private Person generateRandomPerson() {
        return new Person(id++, random(PersonData.FIRST_NAMES), random(PersonData.LAST_NAMES));
    }

    private String random(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
