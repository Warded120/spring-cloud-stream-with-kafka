package com.ihren.processor.serialization.model.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.model.Item;
import com.ihren.processor.serialization.exception.SerializationException;
import io.vavr.control.Try;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ItemSerializer implements Serializer<Item> {

    private final ObjectMapper objectMapper;

    public ItemSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        ObjectMapperConfig.configure(objectMapper);
    }

    @Override
    public byte[] serialize(String s, Item item) {
        return Try.of(() -> objectMapper.writeValueAsBytes(item))
                .getOrElseThrow(e -> new SerializationException("failed to serialize person", e));
    }
}
