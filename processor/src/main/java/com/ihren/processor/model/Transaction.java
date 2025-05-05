package com.ihren.processor.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        String source,
        String discount,
        Long sequenceNumber,
        Instant operationDateTime,
        List<Item> items,
        Total total
) { }
