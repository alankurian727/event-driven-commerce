package com.alankurian.commerce.order.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
        SELECT *
        FROM outbox_events
        WHERE published_at IS NULL
        AND next_attempt_at <= NOW()
        AND (
          claimed_at IS NULL
          OR claimed_at < :staleBefore
        )
        ORDER BY occurred_at
        LIMIT 100
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEvent> findEventsToClaim(@Param("staleBefore") OffsetDateTime staleBefore);
}
