package com.drtx.ecomerce.amazon.core.model.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLog(
        UUID id,
        Long actorId,
        String action,
        String entityType,
        String entityId,
        String metadata,
        Instant occurredAt) {
}
