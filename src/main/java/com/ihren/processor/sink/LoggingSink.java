package com.ihren.processor.sink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingSink implements Sink {
    @Override
    public <T> void apply(T message, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }
}
