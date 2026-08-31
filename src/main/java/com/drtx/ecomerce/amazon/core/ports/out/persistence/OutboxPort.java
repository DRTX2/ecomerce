package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.events.OutboxEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxPort {
    void append(OutboxEvent event);
    List<OutboxEvent> claimPending(Instant now, Instant lockedUntil, int limit);
    void markProcessed(UUID eventId, Instant processedAt);
    void reschedule(UUID eventId, Instant availableAt, String error);
}
