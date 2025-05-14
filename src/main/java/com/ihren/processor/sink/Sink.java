package com.ihren.processor.sink;

public interface Sink<T> {
    //TODO: Message<T>
    void apply(T input, Throwable ex);
}
