package com.ihren.processor.enricher;

public interface Enricher<T> {
    void enrich(T data);
}
