package com.alankurian.commerce.order.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        String currency,
        BigDecimal totalAmount,
        Instant occurredAt
) implements DomainEvent{
}
