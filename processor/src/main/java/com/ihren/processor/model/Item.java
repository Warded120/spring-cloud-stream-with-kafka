package com.ihren.processor.model;

public record Item(
        Long id,
        String account,
        String beginDateTime,
        String endDateTime
) { }
