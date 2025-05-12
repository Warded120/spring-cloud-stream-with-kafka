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
        @NotNull
        Long sequenceNumber,
        @NotBlank
        String endDateTime,
        @NotEmpty
        List<@Valid ItemDto> items,
        @Valid
        @NotNull
        TotalDto total
) { }
