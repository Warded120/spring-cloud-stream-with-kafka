package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExceptionHandler<I, O> {

    private final Sink<String> sink;

    public Try<O> handle(Function<I, O> function, I input) {
        return Try.of(() -> function.apply(input))
            .recover(ex -> {
                //TODO: log the exception that has been thrown and, optionally, the message
                sink.apply(ex.getMessage(), ex);
                return null;
            });
    }
}
