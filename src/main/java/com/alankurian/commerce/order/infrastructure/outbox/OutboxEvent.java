package com.alankurian.commerce.order.infrastructure.outbox;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "claimed_at")
    private OffsetDateTime claimedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "last_attempted_at")
    private OffsetDateTime lastAttemptedAt;

    @Column(name = "next_attempt_at", nullable = false)
    private OffsetDateTime nextAttemptAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String payload,
            OffsetDateTime occurredAt,
            OffsetDateTime nextAttemptAt
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.nextAttemptAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public OffsetDateTime getClaimedAt() {
        return claimedAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public OffsetDateTime getLastAttemptedAt() {
        return lastAttemptedAt;
    }

    public OffsetDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void recordAttempt() {
        this.attemptCount++;
        this.lastAttemptedAt = OffsetDateTime.now();
    }

    public void claim() {
        this.claimedAt = OffsetDateTime.now();
    }

    public void releaseClaim() {
        this.claimedAt = null;
    }

    public void markPublished() {
        this.publishedAt = OffsetDateTime.now();
        this.claimedAt = null;
    }

    public boolean isReadyForRetry(
            OffsetDateTime now,
            Duration initialDelay,
            Duration maxDelay) {

        if (lastAttemptedAt == null) {
            return true;
        }

        var delay = calculateRetryDelay(
                initialDelay,
                maxDelay
        );

        return !lastAttemptedAt.plus(delay).isAfter(now);
    }

    private Duration calculateRetryDelay(
            Duration initialDelay,
            Duration maxDelay) {

        long multiplier = 1L << Math.min(attemptCount - 1, 30);

        Duration delay = initialDelay.multipliedBy(multiplier);

        return delay.compareTo(maxDelay) > 0
                ? maxDelay
                : delay;
    }

    public void scheduleNextAttempt(Duration initialDelay, Duration maxDelay) {

        long multiplier = 1L << Math.min(attemptCount - 1, 30);

        Duration delay = initialDelay.multipliedBy(multiplier);

        if (delay.compareTo(maxDelay) > 0) {
            delay = maxDelay;
        }

        this.nextAttemptAt = OffsetDateTime.now().plus(delay);
    }


}
