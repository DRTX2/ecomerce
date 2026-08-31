package com.drtx.ecomerce.amazon.application.usecases.outbox;

import com.drtx.ecomerce.amazon.core.model.events.OutboxEvent;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OutboxPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxWorker {
    private final OutboxPort outboxPort;

    @Value("${outbox.worker.enabled:true}")
    private boolean enabled;

    @Value("${outbox.worker.batch-size:20}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${outbox.worker.fixed-delay-ms:5000}")
    public void processPendingEvents() {
        if (!enabled) {
            return;
        }

        Instant now = Instant.now();
        outboxPort.claimPending(now, now.plus(Duration.ofMinutes(1)), batchSize)
                .forEach(this::process);
    }

    private void process(OutboxEvent event) {
        try {
            // The local transport is intentionally side-effect free; AWS will replace it with EventBridge/SQS.
            log.info("outbox_event_processed eventId={} eventType={} aggregateId={}", event.id(), event.eventType(),
                    event.aggregateId());
            outboxPort.markProcessed(event.id(), Instant.now());
        } catch (RuntimeException exception) {
            log.warn("outbox_event_failed eventId={} eventType={}", event.id(), event.eventType(), exception);
            outboxPort.reschedule(event.id(), Instant.now().plus(Duration.ofMinutes(1)), exception.getClass().getSimpleName());
        }
    }
}
