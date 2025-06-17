package com.ihren.processor.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihren.processor.constant.Constants;
import com.ihren.processor.constant.ErrorCode;
import com.ihren.processor.exception.ApplicationException;
import com.ihren.processor.exception.SerializationException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicaitonExceptionHeadersCreator implements DeadLetterPublishingRecoverer.ExceptionHeadersCreator {
    private final ObjectMapper mapper;

    @Override
    public void create(Headers kafkaHeaders, Exception exception, boolean isKey, DeadLetterPublishingRecoverer.HeaderNames headerNames) {
        Try.run(() -> {
                    ApplicationException applicationException = getApplicationExceptionFrom(exception);

                    kafkaHeaders.add(Constants.Kafka.Headers.ERROR_CODE, mapper.writeValueAsBytes(applicationException.getErrorCode()));
                    kafkaHeaders.add(Constants.Kafka.Headers.EXCEPTION_MESSAGE, mapper.writeValueAsBytes(applicationException.getMessage()));
                    kafkaHeaders.add(Constants.Kafka.Headers.IS_DLT, mapper.writeValueAsBytes(true));
                })
                .getOrElseThrow(ex -> new SerializationException("Cannot serialize record headers", ex));
    }

    private ApplicationException getApplicationExceptionFrom(Exception exception) {
        return Optional.of(exception)
                .filter(ApplicationException.class::isInstance)
                .map(ApplicationException.class::cast)
                .orElseGet(() ->
                        Optional.of(exception)
                                .map(ex -> (Exception) ex.getCause())
                                .map(this::getApplicationExceptionFrom)
                                .orElse(new ApplicationException(exception.getMessage(), ErrorCode.UNKNOWN))
                );
    }
}
