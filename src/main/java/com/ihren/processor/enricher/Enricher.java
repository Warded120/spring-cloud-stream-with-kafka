package com.ihren.processor.enricher;

public interface Enricher<T> {
    T enrich(T data);
}
