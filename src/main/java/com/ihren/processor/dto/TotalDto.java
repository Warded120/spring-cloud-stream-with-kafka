package com.ihren.processor.dto;

import com.ihren.processor.validation.currency.ValidCurrency;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TotalDto(
        @NotNull(message = "total's amount cannot be null")
        BigDecimal amount,
        @ValidCurrency
        String currency
) { }
