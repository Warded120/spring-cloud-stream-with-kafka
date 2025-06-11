package com.ihren.processor.sink;

//TODO: Do I need it?
public interface Sink {
    <T> void apply(T input, Throwable cause);
}
