package com.ihren.processor.service;

import com.ihren.processor.model.input.InputTransaction;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReplayServiceImplTest {
    @InjectMocks
    private ReplayServiceImpl replayService;
    
    @Mock
    private KafkaConsumer<String, InputTransaction> consumer;
    
    @Mock
    private StreamBridge streamBridge;

    //TODO: finish test
    @Test
    void should() {
    }
}