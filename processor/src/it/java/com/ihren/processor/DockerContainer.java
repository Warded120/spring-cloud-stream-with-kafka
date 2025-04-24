package com.ihren.processor;

public interface DockerContainer<T> {
    void start();

    void destroy();

    T getContainer();
}