package com.ihren.processor.dto;

import com.ihren.processor.validation.currency.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TotalDto(
        @NotNull
        BigDecimal amount,
        @NotBlank
        @ValidCurrency
        String currency
) {
}
