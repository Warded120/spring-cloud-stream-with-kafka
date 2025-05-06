package com.ihren.processor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemDto(
        @NotNull
        Long id,
        //TODO: add custom validation for checking if number is in range: account (String) -> if account Id 1 -> Main, 2 -> Coupon, 3 -> Base, 4 -> Total. For other cases -> invalid
        CharSequence loyaltyAccountId,
        @NotBlank
        String beginDateTime,
        @NotBlank
        String endDateTime
) { }
