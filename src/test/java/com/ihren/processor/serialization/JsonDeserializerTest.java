package com.ihren.processor.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.config.ObjectMapperConfig;
import com.ihren.processor.serialization.exception.SerializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class JsonDeserializerTest {
    @Spy
    @InjectMocks
    //TODO: try Object.class for mocking
    private JsonDeserializer<Object> jsonDeserializer;

    @Mock
    private ObjectMapper objectMapper;

    private MockedStatic<ObjectMapperConfig> objectMapperConfig;

    @BeforeEach
    void setUp() {
        objectMapperConfig = mockStatic(ObjectMapperConfig.class);
        ReflectionTestUtils.setField(jsonDeserializer, "objectMapper", objectMapper);
    }

    @AfterEach
    void tearDown() {
        objectMapperConfig.close();
    }

    @Test
    void should_ReturnType_when_InputIsValid() throws IOException {
        //given
        Object expected = new Object();
        byte[] bytes = new byte[]{1};
        String topic = "topic";

        given(objectMapper.readValue(bytes, Object.class)).willReturn(expected);
        ReflectionTestUtils.setField(jsonDeserializer, "targetClass", Object.class);

        //when
        Object actual = jsonDeserializer.deserialize(topic, bytes);

        //then
        assertEquals(expected, actual);

        then(objectMapper).should().readValue(bytes, Object.class);
    }

    @Test
    void should_ThrowSerializationException_when_InputIsInvalid() throws IOException {
        //given
        byte[] bytes = "data".getBytes();
        String topic = "topic";

        given(objectMapper.readValue(bytes, Object.class)).willThrow(IOException.class);

        //when
        //then
        assertThrows(SerializationException.class, () -> jsonDeserializer.deserialize(topic, bytes));
    }

    @Test
    void should_configureObjectMapper_when_EverythingIsOK() {
        //given
        Map configs = mock();
        boolean isKey = true;

        given(configs.get("value.deserializer.target.class")).willReturn("java.lang.Object");

        //when
        jsonDeserializer.configure(configs, isKey);

        //then
        objectMapperConfig.verify(() -> ObjectMapperConfig.configure(objectMapper));
    }
    @Test
    void should_ThrowSerializationException_when_ClassNotFound() {
        //given
        Map configs = mock();
        boolean isKey = true;

        given(configs.get("value.deserializer.target.class")).willReturn("invalidClass");

        //when
        assertThrows(SerializationException.class, () -> jsonDeserializer.configure(configs, isKey));

        //then
        objectMapperConfig.verify(() -> ObjectMapperConfig.configure(objectMapper), never());
    }
}