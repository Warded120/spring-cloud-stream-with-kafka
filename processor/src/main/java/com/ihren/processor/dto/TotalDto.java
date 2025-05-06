package com.ihren.processor.dto;

import com.ihren.processor.validation.contains.in.ContainsIn;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TotalDto(
        @NotNull
        BigDecimal amount,
        @ContainsIn
        String currency
) { }
