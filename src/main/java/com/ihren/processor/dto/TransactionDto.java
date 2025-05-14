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
        @NotNull(message = "cannot be null")
        Long sequenceNumber,
        @NotBlank(message = "cannot be blank")
        String endDateTime,
        @NotEmpty(message = "list must have at least one element")
        List<@Valid ItemDto> items,
        @Valid
        @NotNull(message = "cannot be null")
        TotalDto total
) { }
