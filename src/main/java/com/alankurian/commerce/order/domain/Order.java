package com.alankurian.commerce.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // Required by JPA
    }

    private Order(
            UUID id,
            UUID customerId,
            String currency
    ) {
        this.id = id;
        this.customerId = customerId;
        this.currency = currency;
        this.status = OrderStatus.CREATED;

        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Order create(
            UUID customerId,
            String currency,
            List<OrderItem> items
    ) {
        var order =  new Order(
                UUID.randomUUID(),
                customerId,
                currency
        );
        order.items.addAll(items);
        order.totalAmount = order.calculateTotal();

        return order;
    }

    public void reserveInventory() {

        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Inventory can only be reserved for a created order"
            );
        }

        status = OrderStatus.INVENTORY_RESERVED;
        updatedAt = OffsetDateTime.now();
    }

    public void markPaymentPending() {

        if (status != OrderStatus.INVENTORY_RESERVED) {
            throw new IllegalStateException(
                    "Payment can only start after inventory reservation"
            );
        }

        status = OrderStatus.PAYMENT_PENDING;
        updatedAt = OffsetDateTime.now();
    }

    public void markPaid() {

        if (status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException(
                    "Order must be awaiting payment"
            );
        }

        status = OrderStatus.PAID;
        updatedAt = OffsetDateTime.now();
    }

    public void confirm() {

        if (status != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Only paid orders can be confirmed"
            );
        }

        status = OrderStatus.CONFIRMED;
        updatedAt = OffsetDateTime.now();
    }

    public void cancel() {

        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Confirmed orders cannot be cancelled"
            );
        }

        status = OrderStatus.CANCELLED;
        updatedAt = OffsetDateTime.now();
    }

    private BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
