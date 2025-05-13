package com.ihren.processor.dto;

import com.ihren.processor.validation.account.id.AccountId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemDto(
        //TODO: add message attributes to all constraints
        @NotNull
        Long id,
        @AccountId
        CharSequence loyaltyAccountId,
        @NotBlank
        String beginDateTime,
        @NotBlank
        String endDateTime
) { }
