package com.ihren.processor.model;

import com.ihren.processor.constant.Currency;

import java.math.BigDecimal;

public record Total(
        BigDecimal amount,
        Currency currency
) { }
