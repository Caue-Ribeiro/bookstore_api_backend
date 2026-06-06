package com.caue.bookstore.utils;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple in-memory token blacklist for logout functionality.
 * In production, this should be replaced with a Redis-based solution for distributed caching.
 */
@Component
public class TokenBlacklist {

    private final Set<String> blacklistedTokens = new HashSet<>();

    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
}

