package com.drtx.ecomerce.amazon.adapters.out.persistence.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
public class OutboxEventEntity {
    @Id
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;
    private Instant occurredAt;
    private Instant availableAt;
    private Instant processedAt;
    private Integer attempts;
    private String lastError;
    private Instant lockedUntil;

    protected OutboxEventEntity() {
    }

    OutboxEventEntity(UUID id, String aggregateType, String aggregateId, String eventType, String payload,
            Instant occurredAt, Instant availableAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.availableAt = availableAt;
        this.attempts = 0;
    }
}
