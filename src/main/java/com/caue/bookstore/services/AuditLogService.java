package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuditLogDTO;
import com.caue.bookstore.entities.AuditLog;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.repositories.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog logAction(User user, String action, String details, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog(user, action, details, ipAddress, userAgent);
        return auditLogRepository.save(auditLog);
    }

    @Transactional
    public AuditLog logAction(User user, String action, String details) {
        AuditLog auditLog = new AuditLog(user, action, details, null, null);
        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getUserAuditLogs(User user, Pageable pageable) {
        return auditLogRepository.findByUserOrderByTimestampDesc(user, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getUserAuditLogsByAction(User user, String action, Pageable pageable) {
        return auditLogRepository.findByUserAndActionOrderByTimestampDesc(user, action, pageable)
                .map(this::toDTO);
    }

    private AuditLogDTO toDTO(AuditLog auditLog) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLog.getId().toString());
        dto.setUserId(auditLog.getUser().getId().toString());
        dto.setAction(auditLog.getAction());
        dto.setDetails(auditLog.getDetails());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setTimestamp(auditLog.getTimestamp());
        dto.setUserAgent(auditLog.getUserAgent());
        return dto;
    }
}
