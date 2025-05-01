package com.ihren.processor.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Transaction(
        //randomly generated UUID
        UUID transactionId,
        //'Softserve' constant always
        String source,
        //to be ignored
        String discount,
        Long sequenceNumber,
        //mapped from endDateTime field
        Instant operationDateTime,
        List<Item> items,
        Total total
) { }
