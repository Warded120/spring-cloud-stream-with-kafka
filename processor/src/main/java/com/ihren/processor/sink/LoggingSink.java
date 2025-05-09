package com.ihren.processor.sink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingSink implements Sink<String> {

    @Override
    public void apply(String message) {
        log.error(message);
    }
}
