package com.alankurian.commerce.order.api;

import com.alankurian.commerce.order.domain.Order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderResponse(UUID id,
                            UUID customerId,
                            String status,
                            BigDecimal totalAmount,
                            String currency,
                            OffsetDateTime createdAt) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedAt()
        );
    }
}
