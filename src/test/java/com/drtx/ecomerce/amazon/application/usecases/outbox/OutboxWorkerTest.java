package com.drtx.ecomerce.amazon.application.usecases.outbox;

import com.drtx.ecomerce.amazon.core.model.events.OutboxEvent;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OutboxPort;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutboxWorkerTest {
    @Test
    void claimsAndMarksPendingEventsAsProcessed() {
        OutboxPort outboxPort = mock(OutboxPort.class);
        OutboxWorker worker = new OutboxWorker(outboxPort);
        ReflectionTestUtils.setField(worker, "enabled", true);
        ReflectionTestUtils.setField(worker, "batchSize", 10);
        OutboxEvent event = new OutboxEvent(UUID.randomUUID(), "ORDER", "1", "OrderConfirmed", "{}",
                Instant.now(), Instant.now());
        when(outboxPort.claimPending(any(), any(), eq(10))).thenReturn(List.of(event));

        worker.processPendingEvents();

        verify(outboxPort).markProcessed(eq(event.id()), any());
        verify(outboxPort, never()).reschedule(any(), any(), any());
    }
}
