package com.caue.bookstore.services;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.InvalidResetTokenException;
import com.caue.bookstore.repositories.UserRepository;
import com.caue.bookstore.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldLockAccountAfterMaxFailedAttempts() {
        User user = new User();
        user.setFailedLoginAttempts(4);
        user.setIsLocked(false);

        Map<String, String> result = userService.handleFailedLoginAttempt(user);

        assertTrue(Boolean.TRUE.equals(user.getIsLocked()));
        assertTrue(user.getLockExpirationTime() != null);
        assertTrue(result.containsKey("status"));
        verify(repository).saveAndFlush(user);
        verify(auditLogService).logAction(eq(user), eq("LOGIN_FAILED"), any(String.class));
    }

    @Test
    void shouldRejectExpiredResetToken() {
        String token = "expired-token";
        User user = new User();
        user.setPasswordResetToken(token);
        user.setResetTokenExpiration(System.currentTimeMillis() - 1000);

        when(repository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        assertThrows(InvalidResetTokenException.class, () -> userService.resetPasswordWithToken(token, "StrongPass1!"));
        verify(repository, never()).save(any(User.class));
    }
}
