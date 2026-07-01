package com.buildsage.service;

import com.buildsage.domain.AuditLog;
import com.buildsage.repository.AuditLogRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(UUID actorId, String action, String resourceType, String resourceId, String details) {
        auditLogRepository.save(new AuditLog(actorId, action, resourceType, resourceId, details));
    }
}
