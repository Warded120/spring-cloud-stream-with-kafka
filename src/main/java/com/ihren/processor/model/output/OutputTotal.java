package com.ihren.processor.model.output;

import com.ihren.processor.constant.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OutputTotal {
    private BigDecimal amount;
    private CurrencyCode currency;
}
