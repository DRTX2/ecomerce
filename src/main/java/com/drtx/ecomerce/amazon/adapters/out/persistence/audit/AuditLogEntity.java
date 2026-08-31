package com.drtx.ecomerce.amazon.adapters.out.persistence.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {
    @Id
    private UUID id;
    private Long actorId;
    private String action;
    private String entityType;
    private String entityId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadata;
    private Instant occurredAt;

    protected AuditLogEntity() {
    }

    AuditLogEntity(UUID id, Long actorId, String action, String entityType, String entityId, String metadata, Instant occurredAt) {
        this.id = id;
        this.actorId = actorId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.metadata = metadata;
        this.occurredAt = occurredAt;
    }
}
