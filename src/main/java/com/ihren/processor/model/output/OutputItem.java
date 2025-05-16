package com.ihren.processor.model.output;

public record OutputItem(
        Long id,
        String account,
        String beginDateTime,
        String endDateTime
) { }
