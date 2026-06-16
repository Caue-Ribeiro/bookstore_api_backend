package com.caue.bookstore.filters;

import com.caue.bookstore.services.UserService;
import com.caue.bookstore.utils.JWTUtil;
import com.caue.bookstore.utils.TokenBlacklist;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;
    private UserService service;
    private HandlerExceptionResolver handlerExceptionResolver;
    private TokenBlacklist tokenBlacklist;

    
    private static final String[] PUBLIC_PATHS = {
            "/authenticate",
            "/log-out",
            "/forgot-password",
            "/reset-password",
            "/api/users/register",
            "/api/authors/summary",
            "/api/books/reader-discovery"
    };

    public JwtAuthFilter(JWTUtil jwtUtil, UserService service,
                         @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
                         TokenBlacklist tokenBlacklist) {
        this.jwtUtil = jwtUtil;
        this.service = service;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        
        String requestPath = request.getServletPath();
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);

                
                if (tokenBlacklist.isBlacklisted(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                username = jwtUtil.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = service.loadUserByUsername(username);

                if (jwtUtil.validateToken(username, userDetails, token)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }


    }

    
    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}
