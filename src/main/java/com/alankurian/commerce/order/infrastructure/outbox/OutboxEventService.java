package com.alankurian.commerce.order.infrastructure.outbox;

import com.alankurian.commerce.order.domain.event.DomainEvent;
import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class OutboxEventService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void save(DomainEvent event) {

        try {
            var payload = objectMapper.writeValueAsString(event);

            var occurredAt = OffsetDateTime.now();

            var outboxEvent = new OutboxEvent(
                    extractEventId(event),
                    event.getClass().getSimpleName(),
                    extractAggregateId(event),
                    event.getClass().getSimpleName(),
                    payload,
                    occurredAt,
                    occurredAt
            );

            repository.save(outboxEvent);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to serialize domain event",
                    e
            );
        }
    }

    private UUID extractAggregateId(DomainEvent event) {
        if (event instanceof com.alankurian.commerce.order.domain.event.OrderCreatedEvent orderCreated) {
            return orderCreated.orderId();
        }

        throw new IllegalArgumentException(
                "Unsupported domain event: " + event.getClass()
        );
    }

    private UUID extractEventId(DomainEvent event) {
        if (event instanceof OrderCreatedEvent orderCreated) {
            return orderCreated.eventId();
        }

        throw new IllegalArgumentException(
                "Unsupported domain event: " + event.getClass()
        );
    }
}
