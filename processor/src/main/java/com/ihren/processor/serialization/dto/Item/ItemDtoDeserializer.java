package com.ihren.processor.serialization.dto.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.dto.ItemDto;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemDtoDeserializer implements Deserializer<ItemDto> {

    private final ObjectMapper objectMapper;

    public ItemDtoDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public ItemDto deserialize(String s, byte[] bytes) {
        return Try.of(() -> objectMapper.readValue(bytes, ItemDto.class))
                .getOrElseThrow(e -> new SerializationException("failed to deserialize person", e));
    }
}