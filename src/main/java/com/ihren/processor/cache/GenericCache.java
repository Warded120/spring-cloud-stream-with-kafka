package com.ihren.processor.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
* Generic cache solution
*/
public class GenericCache<K, V> {
    private final Cache<K, V> cache;

    /**
     * Create Cache without expiration
     */
    public GenericCache() {
        this.cache = Caffeine.newBuilder()
                .build();
    }

    /**
     * Create Cache with expiration
     */
    public GenericCache(Duration expiration) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expiration)
                .build();
    }

    /**
     * Collect all necessary data to save in cache in one action
     *
     * @param loadSupplier Function to collect all necessary data
     */
    public void load(Supplier<Map<K, V>> loadSupplier) {
        loadSupplier.get().forEach(cache::put);
    }

    public void clearCache() {
        cache.invalidateAll();
    }

    /**
     * Get cached data or call real data
     *
     * @param fn Function for real retrieving data
     */
    public Function<K, V> of(Function<K, V> fn) {
        return key -> Optional.ofNullable(key)
                .map(cache::getIfPresent)
                .orElseGet(() -> {
                    V applied = fn.apply(key);
                    cache.put(key, applied);
                    return applied;
                });
    }
}