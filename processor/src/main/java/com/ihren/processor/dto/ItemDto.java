package com.ihren.processor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemDto(
        @NotNull
        Long id,
        CharSequence loyaltyAccountId,
        @NotBlank
        String beginDateTime,
        @NotBlank
        String endDateTime
) { }
