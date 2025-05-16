package com.ihren.processor.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class JsonSerializerTest {
    @InjectMocks
    private JsonSerializer<Object> jsonSerializer;

    @Mock
    private ObjectMapper objectMapper;

    private MockedStatic<ObjectMapperConfig> objectMapperConfig;

    @BeforeEach
    void setUp() {
        objectMapperConfig = mockStatic(ObjectMapperConfig.class);
        ReflectionTestUtils.setField(jsonSerializer, "objectMapper", objectMapper);
    }

    @AfterEach
    void tearDown() {
        objectMapperConfig.close();
    }


    @Test
    void should_ReturnByteArray_when_InputIsValid() throws JsonProcessingException {
        //given
        String data = "data";
        byte[] expected = "Data".getBytes();

        String topic = "topic";
        given(objectMapper.writeValueAsBytes(data)).willReturn(expected);

        //when
        byte[] actual = jsonSerializer.serialize(topic, data);

        //then
        assertArrayEquals(expected, actual);
    }

    @Test
    void should_ThrowSerializationException_whenInputIsInvalid() throws JsonProcessingException {
        //given
        String data = "data";
        String topic = "topic";

        given(objectMapper.writeValueAsBytes(data)).willThrow(JsonProcessingException.class);

        //when
        //then
        assertThrows(SerializationException.class, () -> jsonSerializer.serialize(topic, data));
    }

    @Test
    void should_ConfigureObjectMapper_when_EverythingIsOK() throws JsonProcessingException {
        //given
        Map configs = mock();
        boolean isKey = false;

        //when
        jsonSerializer.configure(configs, isKey);

        //then
        objectMapperConfig.verify(() -> ObjectMapperConfig.configure(objectMapper));
    }
}