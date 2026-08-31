package com.drtx.ecomerce.amazon.adapters.out.persistence.outbox;

import com.drtx.ecomerce.amazon.core.model.events.OutboxEvent;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OutboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxPort {
    private final OutboxEventPersistenceRepository repository;

    @Override
    public void append(OutboxEvent event) {
        repository.save(new OutboxEventEntity(event.id(), event.aggregateType(), event.aggregateId(),
                event.eventType(), event.payload(), event.occurredAt(), event.availableAt()));
    }

    @Override
    @Transactional
    public List<OutboxEvent> claimPending(Instant now, Instant lockedUntil, int limit) {
        return repository.findClaimable(now, PageRequest.of(0, limit)).stream()
                .peek(event -> {
                    event.setLockedUntil(lockedUntil);
                    event.setAttempts(event.getAttempts() + 1);
                })
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void markProcessed(UUID eventId, Instant processedAt) {
        OutboxEventEntity event = repository.findById(eventId).orElseThrow();
        event.setProcessedAt(processedAt);
        event.setLockedUntil(null);
        event.setLastError(null);
    }

    @Override
    @Transactional
    public void reschedule(UUID eventId, Instant availableAt, String error) {
        OutboxEventEntity event = repository.findById(eventId).orElseThrow();
        event.setAvailableAt(availableAt);
        event.setLockedUntil(null);
        event.setLastError(error);
    }

    private OutboxEvent toDomain(OutboxEventEntity event) {
        return new OutboxEvent(event.getId(), event.getAggregateType(), event.getAggregateId(), event.getEventType(),
                event.getPayload(), event.getOccurredAt(), event.getAvailableAt());
    }
}
