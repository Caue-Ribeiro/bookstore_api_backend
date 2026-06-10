package com.caue.bookstore.utils;

import com.caue.bookstore.exceptions.WeakPasswordException;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final String STRONG_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    
    public static void validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            throw new WeakPasswordException("Password cannot be empty.");
        }

        if (password.length() < MIN_LENGTH) {
            throw new WeakPasswordException("Password must be at least " + MIN_LENGTH + " characters long.");
        }

        if (!password.matches(STRONG_PASSWORD_PATTERN)) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).");
        }
    }

    
    public static String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    
    public static boolean isTokenExpired(Long expirationTime) {
        return expirationTime == null || System.currentTimeMillis() > expirationTime;
    }
}
