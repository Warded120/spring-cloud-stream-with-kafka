package com.ihren.processor.dto;

import com.ihren.processor.validation.account.id.AccountId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InputItem(
        @NotNull(message = "item id cannot be null")
        Long id,
        @AccountId
        CharSequence loyaltyAccountId,
        @NotBlank(message = "item begin date time cannot be blank")
        String beginDateTime,
        @NotBlank(message = "item end date time cannot be blank")
        String endDateTime
) { }
