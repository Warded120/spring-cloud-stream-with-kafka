package com.ihren.processor.service;

import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.validation.CommonValidator;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ItemClient client;

    @Mock
    private CommonValidator<ItemResponse> validator;

    @Test
    void should_ReturnItemResponse_when_EverythingIsOK() {
        //given
        Long id = 1L;

        ItemResponse expected = mock();

        given(client.getById(id)).willReturn(expected);
        given(validator.validate(expected)).willReturn(expected);

        //when
        ItemResponse actual = clientService.getByItemId(id);

        //then
        assertEquals(expected, actual);

        then(client).should().getById(id);
        then(validator).should().validate(expected);
    }

    @Test
    void should_ThrowNotFoundException_when_NotFoundByItemId() {
        //given
        Long id = 1L;

        given(client.getById(id)).willThrow(FeignException.class);

        //when
        assertThrows(NotFoundException.class, () -> clientService.getByItemId(id));

        //then

        then(client).should().getById(id);
        then(validator).should(never()).validate(any(ItemResponse.class));
    }
}