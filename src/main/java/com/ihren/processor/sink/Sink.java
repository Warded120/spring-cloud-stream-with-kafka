package com.ihren.processor.sink;

import org.springframework.messaging.Message;

public interface Sink<T> {
    void apply(Message<T> input, Throwable ex);
}
