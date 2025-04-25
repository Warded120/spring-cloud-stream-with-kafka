package com.ihren.processor.container;

public interface DockerContainer<T> {
    void start();

    void destroy();

    T getContainer();
}