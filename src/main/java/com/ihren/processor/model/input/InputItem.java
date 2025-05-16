package com.ihren.processor.model.input;

import com.ihren.processor.validation.account.id.AccountId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InputItem(
        @NotNull(message = "inputTransaction.item.id cannot be null")
        Long id,
        @AccountId
        CharSequence loyaltyAccountId,
        @NotBlank(message = "inputTransaction.item.beginDateTime cannot be blank")
        String beginDateTime,
        @NotBlank(message = "inputTransaction.item.endDateTime cannot be blank")
        String endDateTime
) { }
