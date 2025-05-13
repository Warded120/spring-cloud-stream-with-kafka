package com.ihren.processor.dto;

import com.ihren.processor.validation.currency.ValidCurrency;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TotalDto(
        @NotNull(message = "cannot be null")
        BigDecimal amount,
        @ValidCurrency
        String currency
) { }
