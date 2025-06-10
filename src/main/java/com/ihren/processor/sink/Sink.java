package com.ihren.processor.sink;

//TODO: create reply topic where messages will be sent from dlt to be processed again (trigger it with RestController)
public interface Sink {
    <T> void apply(T input, Throwable cause);
}
