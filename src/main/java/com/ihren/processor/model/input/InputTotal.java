package com.ihren.processor.model.input;

import com.ihren.processor.validation.currency.Currency;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InputTotal(
        @NotNull(message = "total's amount cannot be null")
        BigDecimal amount,
        @Currency
        String currency
) { }
