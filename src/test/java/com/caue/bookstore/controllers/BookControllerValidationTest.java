package com.caue.bookstore.controllers;


import com.caue.bookstore.controllers.exceptionHandler.ControllerExceptionHandler;
import com.caue.bookstore.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookControllerValidationTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        BookController bookController = new BookController(bookService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).setControllerAdvice(new ControllerExceptionHandler()).setValidator(validator).build();
    }

@Test
    void shouldFailToInsertNewBook() throws Exception {

    String invalidJsonPayload = """
            {
                "title": "",
                "price": 19,
                "stock": 85,
                "authorsIds": [
                  1
                ],
                "categoriesIds": [1,4],
                "coverImageUrl": "https://m.media-amazon.com/images/I/918wxhKJaPL._AC_UF1000,1000_QL80_.jpg"
              }
            """;

    mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJsonPayload))
            .andExpect(status().isUnprocessableContent());
}

}
