package com.ihren.processor.model.output;

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
@SuppressWarnings("MemberName")
public class OutputItem {
    private Long id;
    private String account;
    private String beginDateTime;
    private String endDateTime;
    private BigDecimal price;
    private String producer;
    private String description;
    private BigDecimal VATRate;
    private String UOM;
    private String barCode;
}
