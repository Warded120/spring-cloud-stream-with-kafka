package com.ihren.processor.model;

public record Item(
        Long id,
        //if account Id 1 -> Main, 2 -> Coupon, 3 -> Base, 4 -> Total. For other cases -> 'Unknown'
        String account,
        String beginDateTime,
        String endDateTime
) { }
