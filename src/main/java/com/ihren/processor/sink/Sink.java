package com.ihren.processor.sink;

public interface Sink<T> {
    void apply(T input, Throwable cause);
}
