package com.bookstore.management.service;

import com.bookstore.management.model.Admin;
import com.bookstore.management.model.AuditLogEntry;
import com.bookstore.management.repository.AuditLogRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Admin admin, String actionType, String targetType, String targetId, String description) {
        AuditLogEntry entry = new AuditLogEntry(
                IdGenerator.create("LOG"),
                admin.getId(),
                admin.getFullName(),
                actionType,
                targetType,
                targetId,
                description,
                LocalDateTime.now()
        );
        auditLogRepository.save(entry);
    }

    public List<AuditLogEntry> recentEntries(int limit) {
        return auditLogRepository.findAllEntries().stream()
                .sorted(Comparator.comparing(AuditLogEntry::getOccurredAt).reversed())
                .limit(limit)
                .toList();
    }
}
