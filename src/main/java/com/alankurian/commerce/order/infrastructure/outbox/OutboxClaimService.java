package com.alankurian.commerce.order.infrastructure.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OutboxClaimService {

    private final OutboxEventRepository repository;
    private final Duration claimTimeout;
    private final Duration retryInitialDelay;
    private final Duration retryMaxDelay;

    public OutboxClaimService(OutboxEventRepository repository,
          @Value("${outbox.relay.claim-timeout}") Duration claimTimeout,
          @Value("${outbox.relay.retry.initial-delay}") Duration retryInitialDelay,
          @Value("${outbox.relay.retry.max-delay}") Duration retryMaxDelay) {
        this.repository = repository;
        this.claimTimeout = claimTimeout;
        this.retryInitialDelay = retryInitialDelay;
        this.retryMaxDelay = retryMaxDelay;
    }

    @Transactional
    public List<OutboxEvent> claimEvents() {
        var staleBefore = OffsetDateTime.now()
                .minus(claimTimeout);

        var events = repository.findEventsToClaim(staleBefore);

        events.forEach(event -> {
            event.claim();
            event.recordAttempt();
        });

        repository.saveAll(events);

        return events;
    }

}
