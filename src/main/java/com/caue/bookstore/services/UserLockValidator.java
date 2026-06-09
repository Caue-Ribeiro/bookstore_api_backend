package com.caue.bookstore.services;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.UserLockedException;
import com.caue.bookstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to validate and manage user account lock status.
 * Checks if user is locked before authentication.
 * Auto-unlocks users if lock period has expired.
 */
@Service
public class UserLockValidator {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public UserLockValidator(UserRepository userRepository, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Validate lock status before authentication.
     * If user is locked:
     * - Check if lock has expired
     * - If expired, unlock the user and allow login
     * - If not expired, throw UserLockedException
     *
     * @param user The user attempting to login
     * @throws UserLockedException if user is still locked
     */
    @Transactional
    public void validateAndUnlockIfExpired(User user) {
        if (user.getIsLocked()) {
            // Check if lock has expired
            if (user.getLockExpirationTime() != null && System.currentTimeMillis() > user.getLockExpirationTime()) {
                // Lock has expired - unlock the user
                user.setIsLocked(false);
                user.setLockExpirationTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.saveAndFlush(user);
                auditLogService.logAction(user, "ACCOUNT_UNLOCKED", "Account automatically unlocked after lock period expired");
                return; // Allow login to proceed
            }

            // Lock is still active
            long remainingTime = (user.getLockExpirationTime() - System.currentTimeMillis()) / 1000 / 60;
            throw new UserLockedException("Account is locked. Please try again in " + remainingTime + " minutes.");
        }
    }
}

