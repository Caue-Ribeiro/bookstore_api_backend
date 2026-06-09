package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.PasswordResetConfirm;
import com.caue.bookstore.dto.PasswordResetRequest;
import com.caue.bookstore.entities.AuthRequest;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.UserLockedException;
import com.caue.bookstore.services.AuditLogService;
import com.caue.bookstore.services.UserLockValidator;
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
    private final AuditLogService auditLogService;
    private final UserLockValidator lockValidator;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserService userService, 
                          TokenBlacklist tokenBlacklist, AuditLogService auditLogService, UserLockValidator lockValidator) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
        this.auditLogService = auditLogService;
        this.lockValidator = lockValidator;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> generateToken(@RequestBody AuthRequest authRequest) {
            Map<String, String> response = new HashMap<>();
        try {
            // Load user and check lock status BEFORE authentication
            User user = (User) userService.loadUserByUsername(authRequest.getUsername());
            lockValidator.validateAndUnlockIfExpired(user);

            // Proceed with authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            user = (User) authentication.getPrincipal();
            userService.handleSuccessfulLogin(user);

            String token = jwtUtil.generateToken(authRequest.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (UserLockedException ule) {
            // User is locked - re-throw this specific exception to be handled by ControllerExceptionHandler
            throw ule;
        } catch (Exception e) {
            // Handle failed login attempt for other exceptions
            try {
                User user = (User) userService.loadUserByUsername(authRequest.getUsername());
                response = userService.handleFailedLoginAttempt(user);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } catch (UserLockedException ule) {
                // If user gets locked during failed attempt handling, re-throw it
                throw ule;
            } catch (Exception ignored) {
                // Ignore other exceptions and proceed with throwing original exception
            }
            throw e;
        }
    }

    /**
     * Logout endpoint - adds token to blacklist and logs the action
     */
    @PostMapping("/log-out")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.addToBlacklist(token);

            // Log the logout action
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                auditLogService.logAction(user, "LOGOUT", "User logged out successfully");
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
