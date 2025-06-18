package com.ihren.processor.service;

import com.ihren.processor.client.ItemClient;
import com.ihren.processor.client.response.ItemResponse;
import com.ihren.processor.exception.NotFoundException;
import com.ihren.processor.exception.ValidationException;
import com.ihren.processor.validator.CommonValidator;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        Optional<ItemResponse> expectedOpt = Optional.of(expected);

        given(client.getById(id)).willReturn(expectedOpt);
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

        given(client.getById(id)).willReturn(Optional.empty());

        //when
        assertThrows(NotFoundException.class, () -> clientService.getByItemId(id));

        //then

        then(client).should().getById(id);
        then(validator).should(never()).validate(any(ItemResponse.class));
    }

    @Test
    void should_ThrowValidationException_when_ResponseIsInvalid() {
        //given
        Long id = 1L;
        ItemResponse invalidItemResponse = mock(ItemResponse.class);
        Optional<ItemResponse> invalidItemResponseOpt = Optional.of(invalidItemResponse);

        given(client.getById(id)).willReturn(invalidItemResponseOpt);
        given(validator.validate(invalidItemResponse)).willThrow(ValidationException.class);

        //when
        assertThrows(ValidationException.class, () -> clientService.getByItemId(id));

        //then
        then(client).should().getById(id);
        then(validator).should().validate(any(ItemResponse.class));
    }
}