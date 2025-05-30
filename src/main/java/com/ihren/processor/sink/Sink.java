package com.ihren.processor.sink;

//TODO: creare reply topic where messages will be sent to be processed again (trigger it with controller)
public interface Sink {
    <T> void apply(T input, Throwable cause);
}
