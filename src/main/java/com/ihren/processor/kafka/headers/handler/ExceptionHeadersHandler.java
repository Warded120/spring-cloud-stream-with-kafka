package com.ihren.processor.kafka.headers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ExceptionHeadersHandler implements BiFunction<ConsumerRecord<?, ?>, Exception, Headers> {

    private final ObjectMapper mapper;

    @Override
    public Headers apply(ConsumerRecord<?, ?> record, Exception exception) {
        return Try.of(() -> {
                    ApplicationException applicationException = getCause(exception);
                    RecordHeaders headers = new RecordHeaders();
                    headers.add(Constants.Kafka.Headers.ORIGINAL_TOPIC, record.topic().getBytes());
                    headers.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(applicationException.getErrorCode()));
                    headers.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(applicationException.getMessage()));
                    headers.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
                    return headers;
                })
                .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
    }

    private ApplicationException getCause(Exception exception) {
        return Optional.of(exception)
                .filter(ApplicationException.class::isInstance)
                .map(ApplicationException.class::cast)
                .or(() ->
                        Optional.of(exception)
                                .map(ex -> (Exception) ex.getCause())
                                .map(this::getCause)
                )
                .orElse(new ApplicationException(exception.getMessage(), ErrorCode.UNKNOWN));
    }
}
