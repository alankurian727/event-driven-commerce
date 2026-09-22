ALTER TABLE outbox_events
    ADD COLUMN next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL
        DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_outbox_events_next_attempt
    ON outbox_events (next_attempt_at, occurred_at)
    WHERE published_at IS NULL;