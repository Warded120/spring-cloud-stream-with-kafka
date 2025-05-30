package com.ihren.processor.model.output;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OutputTransaction(
    UUID transactionId,
    String source,
    String discount,
    Long sequenceNumber,
    Instant operationDateTime,
    List<OutputItem> items,
    OutputTotal total
) { }
