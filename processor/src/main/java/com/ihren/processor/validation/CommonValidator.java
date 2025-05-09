package com.ihren.processor.validation;

@FunctionalInterface
public interface CommonValidator<T> {
    void validate(T data);
}
