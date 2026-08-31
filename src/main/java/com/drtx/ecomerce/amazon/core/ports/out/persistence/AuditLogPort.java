package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.audit.AuditLog;

public interface AuditLogPort {
    void append(AuditLog auditLog);
}
