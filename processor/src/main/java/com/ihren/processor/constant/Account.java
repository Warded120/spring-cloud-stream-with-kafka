package com.ihren.processor.constant;

import lombok.Getter;
import java.util.stream.Stream;

@Getter
public enum Account {
    MAIN(1, "Main"),
    COUPON(2, "Coupon"),
    BASE(3, "Base"),
    TOTAL(4, "Total");

    private final int id;
    private final String name;

    Account(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String fromId(int id) {
        return Stream.of(values())
                .filter(account -> account.id == id)
                .map(account -> account.name)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown account id: " + id));
    }
}
