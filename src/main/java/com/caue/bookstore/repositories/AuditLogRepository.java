package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.AuditLog;
import com.caue.bookstore.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    Page<AuditLog> findByUserAndActionOrderByTimestampDesc(User user, String action, Pageable pageable);
}

