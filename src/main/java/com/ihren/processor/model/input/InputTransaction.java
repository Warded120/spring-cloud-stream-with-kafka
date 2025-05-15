//TODO: rename package to input and move to model package
package com.ihren.processor.model.input;

import com.ihren.processor.validation.discount.Discount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

//TODO: rename all dto's to have prefix input
public record InputTransaction(
        @Discount
        String discount,
        @NotNull(message = "transaction sequence number cannot be null")
        Long sequenceNumber,
        @NotBlank(message = "transaction end date time cannot be blank")
        String endDateTime,
        @NotEmpty(message = "items list must have at least one element")
        List<@Valid InputItem> items,
        @Valid
        @NotNull(message = "total cannot be null")
        InputTotal total
) { }
