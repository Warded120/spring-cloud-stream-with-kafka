package com.ihren.processor.validation;

public interface CommonValidator<T> {
    T validate(T data);
}
