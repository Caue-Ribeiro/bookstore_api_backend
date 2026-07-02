package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OTPRepository extends JpaRepository<OneTimePassword, Long> {
    OneTimePassword getOneTimePasswordsByUserId(UUID userId);
}
