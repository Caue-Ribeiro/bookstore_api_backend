package com.caue.bookstore.controllers;


import com.caue.bookstore.controllers.exceptionHandler.ControllerExceptionHandler;
import com.caue.bookstore.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerValidationTest {

    @Mock
    CategoryService categoryService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        CategoryController categoryController = new CategoryController(categoryService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .setValidator(validator).build();
    }

    @Test
    void shouldFailInsertAttempt() throws Exception {

    String invalidPayload = """
                {
                    "categoryList": [
                        {
                            "type": ""
                        }
                    ]
                }
                """;

    mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidPayload))
            .andExpect(status().isBadRequest());
    }

}
