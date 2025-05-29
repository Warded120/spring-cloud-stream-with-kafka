package com.ihren.processor.client.response;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemResponse(
        @NotNull(message = "itemResponse.price cannot be null")
        @Positive(message = "itemResponse.price must be positive")
        BigDecimal price,

        @NotBlank(message = "itemResponse.producer cannot be blank")
        String producer,

        @NotBlank(message = "itemResponse.description cannot be blank")
        String description,

        //TODO: why checkstyle doesn't see the violation? (ask on a daily call)
        @NotNull(message = "itemResponse.VATRate cannot be null")
        @DecimalMin(value = "0.0", message = "itemResponse.VATRate must be at least 0.0")
        @DecimalMax(value = "100.0", message = "itemResponse.VATRate must be at most 100.0")
        BigDecimal VATRate,

        String UOM,

        @NotNull(message = "itemResponse.barCode cannot be null")
        @Pattern(regexp = "\\d{14}", message = "itemResponse.barCode must contain exactly 14 digits")
        String barCode
) { }
