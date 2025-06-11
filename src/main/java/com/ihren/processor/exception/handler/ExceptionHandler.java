package com.ihren.processor.exception.handler;

import com.ihren.processor.sink.Sink;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

//TODO: do I need it?
@Component
@RequiredArgsConstructor
public class ExceptionHandler {

    private final Sink sink;

    public <I, O> Try<O> handle(Function<I, O> function, I input) {
        return Try.of(() -> function.apply(input))
            .recover(ex -> {
                sink.apply(input, ex);
                return null;
            });
    }
}
