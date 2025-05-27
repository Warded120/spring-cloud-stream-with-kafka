package com.ihren.processor.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.ihren.processor.client.response.ItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class GenericCacheTest {
    private GenericCache<Long, ItemResponse> genericCache;

    private Cache<Long, ItemResponse> cache;

    @BeforeEach
    public void init() {
        genericCache = new GenericCache<>();
        cache = (Cache<Long, ItemResponse>)ReflectionTestUtils.getField(genericCache, "cache");
    }

    @Test
    void should_LoadDataIntoCache() {
        // given
        ItemResponse itemResponse = mock(ItemResponse.class);
        Supplier<Map<Long, ItemResponse>> supplier = () -> Map.of(1L, itemResponse);

        // when
        genericCache.load(supplier);

        // then
        assertEquals(itemResponse, cache.getIfPresent(1L));
    }

    @Test
    void should_ClearCache_when_called() {
        //given
        ItemResponse itemResponse = mock(ItemResponse.class);
        cache.put(1L, itemResponse);

        //when
        genericCache.clearCache();

        //then
        assertTrue(cache.asMap().isEmpty());
    }

    @Test
    void should_ReturnFunctionValueAndPutInCache_when_CacheIsEmpty() {
        //given
        Function function = mock(Function.class);
        Long key = 1L;
        ItemResponse itemResponse = mock(ItemResponse.class);

        cache.invalidateAll();

        given(function.apply(key)).willReturn(itemResponse);

        //when
        Function loadingFunction = genericCache.of(function);

        //then
        assertEquals(itemResponse, loadingFunction.apply(key));
        assertEquals(itemResponse, cache.getIfPresent(key));
        then(function).should().apply(key);
    }

    @Test
    void should_ReturnCachedValue_when_CacheIsNotEmpty() {
        //given
        Function function = mock(Function.class);
        Long key = 1L;
        ItemResponse itemResponse = mock(ItemResponse.class);

        cache.put(1L, itemResponse);

        //when
        Function loadingFunction = genericCache.of(function);

        //then
        assertEquals(itemResponse, loadingFunction.apply(key));
        then(function).should(never()).apply(key);
    }
}