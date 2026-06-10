package com.caue.bookstore.controllers;

import com.caue.bookstore.controllers.exceptionHandler.ControllerExceptionHandler;
import com.caue.bookstore.dto.OrderDTO;
import com.caue.bookstore.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerValidationTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(orderService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldRejectZeroQuantityOnAddToCart() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        mockMvc.perform(post("/api/orders/users/{userId}/cart/items/{bookId}", userId, bookId)
                        .queryParam("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("must be greater than or equal to 1")));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldAcceptPositiveQuantityOnAddToCart() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        when(orderService.addToCart(any(), any(), anyInt())).thenReturn(new OrderDTO());

        mockMvc.perform(post("/api/orders/users/{userId}/cart/items/{bookId}", userId, bookId)
                        .queryParam("quantity", "1"))
                .andExpect(status().isCreated());

        verify(orderService).addToCart(any(), any(), anyInt());
    }
}
