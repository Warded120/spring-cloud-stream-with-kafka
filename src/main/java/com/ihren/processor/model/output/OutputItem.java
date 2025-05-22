package com.ihren.processor.model.output;

import java.math.BigDecimal;

//TODO: change OutputItem to class
public record OutputItem(
        Long id,
        String account,
        String beginDateTime,
        String endDateTime,
        BigDecimal price,
        String producer,
        String description,
        BigDecimal VATRate,
        String UOM,
        String BarCode
) { }
