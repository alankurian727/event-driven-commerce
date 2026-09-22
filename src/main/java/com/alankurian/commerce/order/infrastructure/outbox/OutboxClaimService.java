package com.alankurian.commerce.order.infrastructure.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxClaimService {

    private final OutboxEventRepository repository;

    public OutboxClaimService(OutboxEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<OutboxEvent> claimEvents() {

        var events = repository.findEventsToClaim();

        events.forEach(OutboxEvent::claim);

        repository.saveAll(events);

        return events;
    }

}
