package com.ihren.processor.constant;

import lombok.Getter;
import java.util.stream.Stream;

@Getter
public enum Account {
    MAIN("1", "Main"),
    COUPON("2", "Coupon"),
    BASE("3", "Base"),
    TOTAL("4", "Total");

    private final String id;
    private final String name;

    Account(String id, String name) {
        this.id = id;
        this.name = name;
    }

    //TODO: maybe override valueOf() method
    public static String fromId(String id) {
        return Stream.of(values())
                .filter(account -> account.id.equals(id))
                .map(account -> account.name)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown account id: " + id));
    }
}
