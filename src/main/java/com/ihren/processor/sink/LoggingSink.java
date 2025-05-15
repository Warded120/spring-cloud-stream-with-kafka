package com.ihren.processor.sink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingSink<T> implements Sink<T> {
    @Override
    public void apply(Message<T> message, Throwable ex) {
        log.error(ex.getMessage(), ex);
    }
}
