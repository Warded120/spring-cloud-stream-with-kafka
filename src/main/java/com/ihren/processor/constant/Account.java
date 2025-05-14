package com.ihren.processor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Account {
    MAIN("1", "Main"),
    COUPON("2", "Coupon"),
    BASE("3", "Base"),
    TOTAL("4", "Total");

    private final String id;
    private final String name;

    public static String getNameById(String id) {
        return Stream.of(values())
                .filter(account -> account.id.equals(id))
                .map(account -> account.name)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown account id: " + id));
    }
}
