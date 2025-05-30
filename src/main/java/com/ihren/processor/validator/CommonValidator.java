package com.ihren.processor.validator;

public interface CommonValidator<T> {
    T validate(T data);
}
