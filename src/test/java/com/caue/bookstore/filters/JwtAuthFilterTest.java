package com.caue.bookstore.filters;

import com.caue.bookstore.services.UserService;
import com.caue.bookstore.utils.JWTUtil;
import com.caue.bookstore.utils.TokenBlacklist;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void shouldSkipAuthenticationWhenTokenIsBlacklisted() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userService, handlerExceptionResolver, tokenBlacklist);

        when(request.getServletPath()).thenReturn("/api/orders/users/1/cart");
        when(request.getHeader("Authorization")).thenReturn("Bearer blacklisted-token");
        when(tokenBlacklist.isBlacklisted("blacklisted-token")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(any());
        verify(userService, never()).loadUserByUsername(any());
    }

    @Test
    void shouldResolveExpiredTokenException() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userService, handlerExceptionResolver, tokenBlacklist);

        when(request.getServletPath()).thenReturn("/api/orders/users/1/cart");
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(tokenBlacklist.isBlacklisted("expired-token")).thenReturn(false);
        when(jwtUtil.extractUsername("expired-token")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        filter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(), any(ExpiredJwtException.class));
        verify(filterChain, never()).doFilter(request, response);
    }
}
