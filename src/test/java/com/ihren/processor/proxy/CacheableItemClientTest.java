package com.ihren.processor.proxy;

import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.CacheableItemClient;
import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CacheableItemClientTest {
    @InjectMocks
    private CacheableItemClient cacheableItemClient;

    @Mock
    private ItemClient originalItemClient;

    @Mock
    private GenericCache<Long, ItemResponse> cache;

    @Test
    void should_GetById_when_EverythingIsOK() {
        //given
        Function loadingFunction = mock(Function.class);
        ItemResponse expected = mock(ItemResponse.class);

        given(cache.of(any(Function.class))).willReturn(loadingFunction);
        given(loadingFunction.apply(1L)).willReturn(expected);

        //when
        ItemResponse actual = cacheableItemClient.getById(1L);

        //then
        assertEquals(expected, actual);
        then(loadingFunction).should().apply(1L);
    }
}