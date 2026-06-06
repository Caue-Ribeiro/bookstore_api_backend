package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.PasswordResetConfirm;
import com.caue.bookstore.dto.PasswordResetRequest;
import com.caue.bookstore.entities.AuthRequest;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.services.UserService;
import com.caue.bookstore.utils.JWTUtil;
import com.caue.bookstore.utils.TokenBlacklist;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final TokenBlacklist tokenBlacklist;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserService userService, TokenBlacklist tokenBlacklist) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            userService.handleSuccessfulLogin(user);

            String token = jwtUtil.generateToken(authRequest.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle failed login attempt
            try {
                User user = (User) userService.loadUserByUsername(authRequest.getUsername());
                userService.handleFailedLoginAttempt(user);
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    /**
     * Logout endpoint - adds token to blacklist
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.addToBlacklist(token);

            // Log the logout action
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                // We can log this via AuditLogService if needed
            }

            SecurityContextHolder.clearContext();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "No token found");
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Request password reset - generates reset token
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody PasswordResetRequest request) {
        userService.requestPasswordReset(request.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("message", "If an account exists with that email, a password reset link will be sent.");
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetConfirm request) {
        userService.resetPasswordWithToken(request.getToken(), request.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been successfully reset. You can now login with your new password.");
        return ResponseEntity.ok(response);
    }
}
