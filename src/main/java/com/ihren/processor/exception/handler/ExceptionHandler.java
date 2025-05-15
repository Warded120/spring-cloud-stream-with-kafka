package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExceptionHandler<I, O> {

    private final Sink<I> sink;

    public Try<O> handle(Function<Message<I>, O> function, Message<I> input) {
        return Try.of(() -> function.apply(input))
            .recover(ex -> {
                sink.apply(input, ex);
                return null;
            });
    }
}
