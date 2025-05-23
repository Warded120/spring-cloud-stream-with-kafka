package com.ihren.processor.model.output;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OutputTransaction {
    private UUID transactionId;
    private String source;
    private String discount;
    private Long sequenceNumber;
    private Instant operationDateTime;
    private List<OutputItem> items;
    private OutputTotal total;
}
