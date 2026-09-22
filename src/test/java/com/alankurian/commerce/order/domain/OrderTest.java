package com.alankurian.commerce.order.domain;

import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void creatingOrderProducesOrderCreatedEvent() {

        var customerId = UUID.randomUUID();

        var items = List.of(
                OrderItem.create(
                        UUID.randomUUID(),
                        2,
                        new BigDecimal("10.00")
                )
        );

        var order = Order.create(
                customerId,
                "EUR",
                items
        );

        assertThat(order.domainEvents())
                .hasSize(1);

        var event = order.domainEvents().getFirst();

        assertThat(event)
                .isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    void creatingOrderProducesCorrectOrderCreatedEvent() {

        var customerId = UUID.randomUUID();

        var items = List.of(
                OrderItem.create(
                        UUID.randomUUID(),
                        2,
                        new BigDecimal("10.00")
                )
        );

        var order = Order.create(
                customerId,
                "EUR",
                items
        );

        var event = (OrderCreatedEvent)
                order.domainEvents().getFirst();

        assertThat(event.orderId())
                .isEqualTo(order.getId());

        assertThat(event.customerId())
                .isEqualTo(customerId);

        assertThat(event.currency())
                .isEqualTo("EUR");

        assertThat(event.totalAmount())
                .isEqualByComparingTo(order.getTotalAmount());

        assertThat(event.occurredAt())
                .isNotNull();
    }
}