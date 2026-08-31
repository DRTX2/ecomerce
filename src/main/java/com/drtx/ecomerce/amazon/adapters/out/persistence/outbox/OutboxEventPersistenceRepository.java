package com.drtx.ecomerce.amazon.adapters.out.persistence.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface OutboxEventPersistenceRepository extends JpaRepository<OutboxEventEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select event from OutboxEventEntity event
             where event.processedAt is null
               and event.availableAt <= :now
               and (event.lockedUntil is null or event.lockedUntil < :now)
             order by event.occurredAt
            """)
    List<OutboxEventEntity> findClaimable(Instant now, Pageable pageable);
}
