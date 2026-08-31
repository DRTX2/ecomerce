package com.drtx.ecomerce.amazon.adapters.out.persistence.audit;

import com.drtx.ecomerce.amazon.core.model.audit.AuditLog;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.AuditLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogPort {
    private final AuditLogPersistenceRepository repository;

    @Override
    public void append(AuditLog auditLog) {
        repository.save(new AuditLogEntity(auditLog.id(), auditLog.actorId(), auditLog.action(),
                auditLog.entityType(), auditLog.entityId(), auditLog.metadata(), auditLog.occurredAt()));
    }
}
