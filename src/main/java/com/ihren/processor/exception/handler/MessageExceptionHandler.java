package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageExceptionHandler<I, O> extends ExceptionHandler<Message<I>, Message<O>> {
    public MessageExceptionHandler(Sink<Message<I>> sink) {
        super(sink);
    }
}
