package com.ihren.processor.dto;

import com.ihren.processor.validation.discount.Discount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TransactionDto(
        @Discount
        String discount,
        @NotNull(message = "transaction sequence number cannot be null")
        Long sequenceNumber,
        @NotBlank(message = "transaction end date time cannot be blank")
        String endDateTime,
        @NotEmpty(message = "items list must have at least one element")
        List<@Valid ItemDto> items,
        @Valid
        @NotNull(message = "total cannot be null")
        TotalDto total
) { }
