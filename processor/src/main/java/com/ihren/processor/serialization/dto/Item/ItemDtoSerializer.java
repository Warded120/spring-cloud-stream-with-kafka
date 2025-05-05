package com.ihren.processor.serialization.dto.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ItemDtoSerializer implements Serializer<ItemDto> {

    private final ObjectMapper objectMapper;

    public ItemDtoSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, ItemDto item) {
        return Try.of(() -> objectMapper.writeValueAsBytes(item))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
