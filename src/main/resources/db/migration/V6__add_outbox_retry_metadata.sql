ALTER TABLE outbox_events
    ADD COLUMN attempt_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE outbox_events
    ADD COLUMN last_attempted_at TIMESTAMP WITH TIME ZONE NULL;

CREATE INDEX idx_outbox_events_retry
    ON outbox_events (published_at, last_attempted_at)
    WHERE published_at IS NULL;