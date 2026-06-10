package com.caue.bookstore.controllers;

import com.caue.bookstore.controllers.exceptionHandler.ControllerExceptionHandler;
import com.caue.bookstore.services.AuditLogService;
import com.caue.bookstore.services.UserLockValidator;
import com.caue.bookstore.services.UserService;
import com.caue.bookstore.utils.JWTUtil;
import com.caue.bookstore.utils.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerValidationTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserLockValidator userLockValidator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(
                authenticationManager,
                jwtUtil,
                userService,
                tokenBlacklist,
                auditLogService,
                userLockValidator
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldRejectBlankCredentialsOnAuthenticate() throws Exception {
        String body = "{\"username\":\"\",\"password\":\"\"}";

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("username")))
                .andExpect(content().string(containsString("password")));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldRejectInvalidEmailOnForgotPassword() throws Exception {
        String body = "{\"email\":\"invalid-email\"}";

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("email")));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldRejectBlankTokenAndPasswordOnResetPassword() throws Exception {
        String body = "{\"token\":\"\",\"newPassword\":\"\"}";

        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("token")))
                .andExpect(content().string(containsString("newPassword")));

        verifyNoInteractions(userService);
    }
}
