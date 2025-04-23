package com.ihren.misc.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperFactory {
    public static ObjectMapper create() {
        return new ObjectMapper();
    }
}
