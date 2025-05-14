package com.ihren.processor.model;

import com.ihren.processor.constant.CurrencyCode;

import java.math.BigDecimal;

public record Total(
        BigDecimal amount,
        CurrencyCode currency
) { }
