package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuditLogDTO;
import com.caue.bookstore.entities.AuditLog;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.repositories.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService service;

    private User user = null;


    @BeforeEach
    void defaultValue(){
        user = new User();
        user.setId(UUID.randomUUID());
    }

    @Test
    void shouldLogActionWithIpAndUserAgent() {


        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            log.setId(UUID.randomUUID());
            return log;
        });

        AuditLog result = service.logAction(user, "LOGIN", "User logged in", "192.168.1.10", "JUnit-Agent");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog savedLog = captor.getValue();

        assertNotNull(result.getId());
        assertEquals(user, savedLog.getUser());
        assertEquals("LOGIN", savedLog.getAction());
        assertEquals("User logged in", savedLog.getDetails());
        assertEquals("192.168.1.10", savedLog.getIpAddress());
        assertEquals("JUnit-Agent", savedLog.getUserAgent());
        assertNotNull(savedLog.getTimestamp());
    }

    @Test
    void shouldLogActionWithoutIpAndUserAgent() {


        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            log.setId(UUID.randomUUID());
            return log;
        });

        AuditLog result = service.logAction(user, "REGISTER", "User created account");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog savedLog = captor.getValue();

        assertNotNull(result.getId());
        assertEquals(user, savedLog.getUser());
        assertEquals("REGISTER", savedLog.getAction());
        assertEquals("User created account", savedLog.getDetails());
        assertNull(savedLog.getIpAddress());
        assertNull(savedLog.getUserAgent());
        assertNotNull(savedLog.getTimestamp());
    }

    @Test
    void shouldGetUserAuditLogs() {
        UUID auditLogId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T10:15:30Z");

        AuditLog auditLog = new AuditLog(user, "LOGIN", "User logged in", "10.0.0.1", "Chrome");
        auditLog.setId(auditLogId);
        auditLog.setTimestamp(timestamp);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(List.of(auditLog), pageable, 1);

        when(auditLogRepository.findByUserOrderByTimestampDesc(user, pageable)).thenReturn(page);

        Page<AuditLogDTO> result = service.getUserAuditLogs(user, pageable);

        assertEquals(1, result.getTotalElements());
        AuditLogDTO dto = result.getContent().getFirst();
        assertEquals(auditLogId.toString(), dto.getId());
        assertEquals(user.getId().toString(), dto.getUserId());
        assertEquals("LOGIN", dto.getAction());
        assertEquals("User logged in", dto.getDetails());
        assertEquals("10.0.0.1", dto.getIpAddress());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals("Chrome", dto.getUserAgent());
        verify(auditLogRepository).findByUserOrderByTimestampDesc(user, pageable);
    }

    @Test
    void shouldGetUserAuditLogsByAction() {
        UUID auditLogId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-02-05T08:00:00Z");

        AuditLog auditLog = new AuditLog(user, "PASSWORD_RESET", "Requested reset", "10.0.0.2", "Firefox");
        auditLog.setId(auditLogId);
        auditLog.setTimestamp(timestamp);

        PageRequest pageable = PageRequest.of(0, 5);
        Page<AuditLog> page = new PageImpl<>(List.of(auditLog), pageable, 1);

        when(auditLogRepository.findByUserAndActionOrderByTimestampDesc(user, "PASSWORD_RESET", pageable)).thenReturn(page);

        Page<AuditLogDTO> result = service.getUserAuditLogsByAction(user, "PASSWORD_RESET", pageable);

        assertEquals(1, result.getTotalElements());
        AuditLogDTO dto = result.getContent().getFirst();
        assertEquals(auditLogId.toString(), dto.getId());
        assertEquals(user.getId().toString(), dto.getUserId());
        assertEquals("PASSWORD_RESET", dto.getAction());
        assertEquals("Requested reset", dto.getDetails());
        assertEquals("10.0.0.2", dto.getIpAddress());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals("Firefox", dto.getUserAgent());
        verify(auditLogRepository).findByUserAndActionOrderByTimestampDesc(user, "PASSWORD_RESET", pageable);
    }
}
