package com.ihren.processor.client;

import com.ihren.processor.cache.GenericCache;
import com.ihren.processor.client.response.ItemResponse;
import org.apache.zookeeper.Op;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private GenericCache<Long, Optional<ItemResponse>> cache;

    @Captor
    private ArgumentCaptor<Function<Long, Optional<ItemResponse>>> captor;

    @Test
    void should_GetById_when_EverythingIsOK() {
        //given
        Long id = 1L;
        Optional<ItemResponse> expected = Optional.of(mock(ItemResponse.class));

        Function loadingFunction = mock(Function.class);

        given(cache.of(captor.capture())).willReturn(loadingFunction);
        given(loadingFunction.apply(id)).willReturn(expected);
        given(originalItemClient.getById(id)).willReturn(expected);

        //when
        Optional<ItemResponse> actual = cacheableItemClient.getById(id);

        //then
        assertEquals(expected, actual);
        then(loadingFunction).should().apply(id);

        Optional<ItemResponse> applied = captor.getValue().apply(id);
        assertEquals(expected, applied);
    }
}