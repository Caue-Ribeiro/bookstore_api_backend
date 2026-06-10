package com.caue.bookstore.services;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.UserLockedException;
import com.caue.bookstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserLockValidator {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public UserLockValidator(UserRepository userRepository, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    
    @Transactional
    public void validateAndUnlockIfExpired(User user) {
        if (user.getIsLocked()) {
            
            if (user.getLockExpirationTime() != null && System.currentTimeMillis() > user.getLockExpirationTime()) {
                
                user.setIsLocked(false);
                user.setLockExpirationTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.saveAndFlush(user);
                auditLogService.logAction(user, "ACCOUNT_UNLOCKED", "Account automatically unlocked after lock period expired");
                return; 
            }

            
            long remainingTime = (user.getLockExpirationTime() - System.currentTimeMillis()) / 1000 / 60;
            throw new UserLockedException("Account is locked. Please try again in " + remainingTime + " minutes.");
        }
    }
}

