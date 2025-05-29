package com.ihren.processor.model.output;

import com.ihren.processor.constant.CurrencyCode;
import java.math.BigDecimal;

public record OutputTotal(
    BigDecimal amount,
    CurrencyCode currency
) { }
