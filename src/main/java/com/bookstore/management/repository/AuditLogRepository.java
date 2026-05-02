package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.AuditLogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogRepository extends JsonLineFileRepository<AuditLogEntry> {

    public AuditLogRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getAuditFile(), AuditLogEntry.class);
    }

    public List<AuditLogEntry> findAllEntries() {
        return readAll();
    }

    public void save(AuditLogEntry entry) {
        store(entry, AuditLogEntry::getId);
    }

    public long count() {
        return readAll().size();
    }
}
