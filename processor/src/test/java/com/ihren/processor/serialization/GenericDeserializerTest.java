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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class GenericDeserializerTest {
    @Spy
    @InjectMocks
    private GenericDeserializer genericDeserializer;

    @Mock
    private ObjectMapper objectMapper;

    private MockedStatic<ObjectMapperConfig> objectMapperConfig;

    @BeforeEach
    void setUp() {
        objectMapperConfig = mockStatic(ObjectMapperConfig.class);
        ReflectionTestUtils.setField(genericDeserializer, "objectMapper", objectMapper);
    }

    @AfterEach
    void tearDown() {
        objectMapperConfig.close();
    }

    @Test
    void should_ReturnType_when_InputIsValid() throws IOException {
        //given
        String expected = "data";
        byte[] bytes = "Data".getBytes();
        String topic = "topic";

        given(objectMapper.readValue(bytes, (Class)null)).willReturn(expected);

        //when
        String actual = (String)genericDeserializer.deserialize(topic, bytes);

        //then
        assertEquals(expected, actual);

        then(objectMapper).should().readValue(bytes, (Class)null);
    }

    @Test
    void should_ThrowSerializationException_when_InputIsInvalid() throws IOException {
        //given
        byte[] bytes = "data".getBytes();
        String topic = "topic";

        given(objectMapper.readValue(bytes, (Class)null)).willThrow(IOException.class);

        //when
        //then
        assertThrows(SerializationException.class, () -> genericDeserializer.deserialize(topic, bytes));
    }

    @Test
    void should_configureObjectMapper_when_EverythingIsOK() throws ClassNotFoundException {
        //given
        Map configs = mock();
        boolean isKey = true;

        given(configs.get("value.deserializer.target.class")).willReturn("targetClass");
        doReturn(String.class).when(genericDeserializer).getTargetClass("targetClass");

        //when
        genericDeserializer.configure(configs, isKey);

        //then
        objectMapperConfig.verify(() -> ObjectMapperConfig.configure(objectMapper));
    }
    @Test
    void should_ThrowSerializationException_when_ClassNotFound() throws ClassNotFoundException {
        //given
        Map configs = mock();
        boolean isKey = true;

        given(configs.get("value.deserializer.target.class")).willReturn("invalidClass");
        doThrow(ClassNotFoundException.class).when(genericDeserializer).getTargetClass("invalidClass");

        //when
        assertThrows(SerializationException.class, () -> genericDeserializer.configure(configs, isKey));

        //then
        objectMapperConfig.verify(() -> ObjectMapperConfig.configure(objectMapper), never());
    }
}