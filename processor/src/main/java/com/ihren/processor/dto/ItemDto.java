package com.ihren.processor.dto;

import com.ihren.processor.validation.contains.in.ContainsIn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemDto(
        @NotNull
        Long id,
        @ContainsIn({"1", "2", "3", "4"})
        CharSequence loyaltyAccountId,
        @NotBlank
        String beginDateTime,
        @NotBlank
        String endDateTime
) { }
