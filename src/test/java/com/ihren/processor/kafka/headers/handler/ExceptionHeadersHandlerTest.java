package com.ihren.processor.kafka.headers.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import io.vavr.collection.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ExceptionHeadersHandlerTest {
    @InjectMocks
    private ExceptionHeadersHandler exceptionHeadersHandler;

    @Mock
    private ObjectMapper mapper;

    @Test
    void should_ReturnHeaders_when_ApplicationExceptionIsPresent() throws JsonProcessingException {
        //given
        String originalTopic = "topic";
        String exceptionMessage = "message";

        byte[] originalTopicBytes = new byte[]{0};
        byte[] errorCodeBytes = new byte[]{1};
        byte[] exceptionMessageBytes = new byte[]{2};
        byte[] isDltBytes = new byte[]{3};

        Headers expected = new RecordHeaders(
                List.of(
                        new RecordHeader(Constants.Kafka.Headers.ORIGINAL_TOPIC, originalTopicBytes),
                        new RecordHeader(Constants.Kafka.Headers.ERROR_CODE, errorCodeBytes),
                        new RecordHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE, exceptionMessageBytes),
                        new RecordHeader(Constants.Kafka.Headers.IS_DLT, isDltBytes)
                )
        );

        ConsumerRecord record = mock(ConsumerRecord.class);
        given(record.topic()).willReturn(originalTopic);

        ApplicationException applicationException = mock(ApplicationException.class);
        given(applicationException.getMessage()).willReturn(exceptionMessage);
        given(applicationException.getErrorCode()).willReturn(ErrorCode.NOT_FOUND);

        Exception exception = mock(Exception.class);
        given(exception.getCause()).willReturn(applicationException);

        given(mapper.writeValueAsBytes(originalTopic)).willReturn(originalTopicBytes);
        given(mapper.writeValueAsBytes(ErrorCode.NOT_FOUND)).willReturn(errorCodeBytes);
        given(mapper.writeValueAsBytes(exceptionMessage)).willReturn(exceptionMessageBytes);
        given(mapper.writeValueAsBytes(true)).willReturn(isDltBytes);

        //when
        Headers actual = exceptionHeadersHandler.apply(record, exception);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void should_ReturnHeaders_when_ApplicationExceptionIsAbsent() throws JsonProcessingException {
        //given
        String originalTopic = "topic";
        String exceptionMessage = "message";

        byte[] originalTopicBytes = new byte[]{0};
        byte[] errorCodeBytes = new byte[]{1};
        byte[] exceptionMessageBytes = new byte[]{2};
        byte[] isDltBytes = new byte[]{3};

        Headers expected = new RecordHeaders(
                List.of(
                        new RecordHeader(Constants.Kafka.Headers.ORIGINAL_TOPIC, originalTopicBytes),
                        new RecordHeader(Constants.Kafka.Headers.ERROR_CODE, errorCodeBytes),
                        new RecordHeader(Constants.Kafka.Headers.EXCEPTION_MESSAGE, exceptionMessageBytes),
                        new RecordHeader(Constants.Kafka.Headers.IS_DLT, isDltBytes)
                )
        );

        ConsumerRecord record = mock(ConsumerRecord.class);
        given(record.topic()).willReturn(originalTopic);

        Exception exception = mock(Exception.class);
        given(exception.getMessage()).willReturn(exceptionMessage);

        given(mapper.writeValueAsBytes(originalTopic)).willReturn(originalTopicBytes);
        given(mapper.writeValueAsBytes(ErrorCode.UNKNOWN)).willReturn(errorCodeBytes);
        given(mapper.writeValueAsBytes(exceptionMessage)).willReturn(exceptionMessageBytes);
        given(mapper.writeValueAsBytes(true)).willReturn(isDltBytes);

        //when
        Headers actual = exceptionHeadersHandler.apply(record, exception);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void should_ThrowSerializationException_when_MappingHeadersFailed() throws JsonProcessingException {
        //given
        String originalTopic = "topic";
        String exceptionMessage = "message";

        ConsumerRecord record = mock(ConsumerRecord.class);
        given(record.topic()).willReturn(originalTopic);

        Exception exception = mock(Exception.class);
        given(exception.getMessage()).willReturn(exceptionMessage);

        given(mapper.writeValueAsBytes(originalTopic)).willThrow(JsonProcessingException.class);

        //when
        assertThrows(SerializationException.class, () -> exceptionHeadersHandler.apply(record, exception));
    }
}