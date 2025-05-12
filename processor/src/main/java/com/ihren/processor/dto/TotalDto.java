package com.ihren.processor.dto;

import com.ihren.processor.validation.contains.in.ContainsIn;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TotalDto(
        @NotNull
        BigDecimal amount,
        @ContainsIn //TODO: create separate annotation (e.g. @Currency), allowedCurrencies should be initialized inside annotation implementation with help of some enum (e.g. Currency.values() )
        String currency
) { }
