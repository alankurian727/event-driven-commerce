package com.alankurian.commerce.order.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxRelay {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxRelay.class);

    private static final String TOPIC = "orders.created";

    private final OutboxClaimService claimService;
    private final OutboxPublisher publisher;

    public OutboxRelay(OutboxClaimService claimService,
                       OutboxPublisher publisher
    ) {
        this.claimService = claimService;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        var events = claimService.claimEvents();

        events.forEach(this::publish);
    }

    private void publish(OutboxEvent event) {
        List<OutboxEvent> events =
                claimService.claimEvents();

        log.debug("Claimed {} outbox events", events.size());

        events.forEach(publisher::publish);

    }

}
