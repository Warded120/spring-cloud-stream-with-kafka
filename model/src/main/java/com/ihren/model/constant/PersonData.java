package com.ihren.model.constant;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PersonData {
    public static final List<String> FIRST_NAMES = List.of(
            "Alice", "Bob", "Charlie", "Diana", "Ethan",
            "Fiona", "George", "Hannah", "Ian", "Julia"
    );

    public static final List<String> LAST_NAMES = List.of(
            "Smith", "Johnson", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"
    );
}
