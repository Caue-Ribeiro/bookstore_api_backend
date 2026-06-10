package com.caue.bookstore.services;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.UserLockedException;
import com.caue.bookstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserLockValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserLockValidator userLockValidator;

    @Test
    void shouldAutoUnlockUserWhenLockHasExpired() {
        User user = new User();
        user.setIsLocked(true);
        user.setFailedLoginAttempts(5);
        user.setLockExpirationTime(System.currentTimeMillis() - 1_000);

        userLockValidator.validateAndUnlockIfExpired(user);

        assertFalse(Boolean.TRUE.equals(user.getIsLocked()));
        assertNull(user.getLockExpirationTime());
        verify(userRepository).saveAndFlush(user);
        verify(auditLogService).logAction(eq(user), eq("ACCOUNT_UNLOCKED"), any(String.class));
    }

    @Test
    void shouldThrowWhenUserIsStillLocked() {
        User user = new User();
        user.setIsLocked(true);
        user.setFailedLoginAttempts(5);
        user.setLockExpirationTime(System.currentTimeMillis() + 10 * 60 * 1000);

        assertThrows(UserLockedException.class, () -> userLockValidator.validateAndUnlockIfExpired(user));
        verify(userRepository, never()).saveAndFlush(user);
    }
}
