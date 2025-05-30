package com.ihren.processor.model.input;

import com.ihren.processor.validator.discount.Discount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record InputTransaction(
        @Discount
        String discount,
        @NotNull(message = "inputTransaction.sequenceNumber cannot be null")
        Long sequenceNumber,
        @NotBlank(message = "inputTransaction.endDateTime cannot be blank")
        String endDateTime,
        @NotEmpty(message = "inputTransaction.items must have at least one element")
        List<@Valid InputItem> items,
        @Valid
        @NotNull(message = "inputTransaction.total cannot be null")
        InputTotal total
) { }
