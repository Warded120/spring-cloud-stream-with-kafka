package com.ihren.processor.dto;

import com.ihren.processor.validation.account.id.AccountId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemDto(
        @NotNull(message = "cannot be null")
        Long id,
        @AccountId
        CharSequence loyaltyAccountId,
        @NotBlank(message = "cannot be blank")
        String beginDateTime,
        @NotBlank(message = "cannot be blank")
        String endDateTime
) { }
