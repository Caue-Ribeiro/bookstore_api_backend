package com.caue.bookstore.config;

import com.caue.bookstore.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityChecker")
public class SecurityChecker {

    private UserRepository userRepository;

    public SecurityChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUserOwner(Authentication authentication, UUID targetId) {

        String currentUser = authentication.getName();
        System.out.println("DEBUG - Current Authenticated User is: [" + currentUser + "]");

        return userRepository.findById(targetId).map(user -> user.getEmail().equals(currentUser)).orElse(false);

    }
}