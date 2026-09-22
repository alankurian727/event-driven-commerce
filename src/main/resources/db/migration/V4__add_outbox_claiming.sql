ALTER TABLE outbox_events
    ADD COLUMN claimed_at TIMESTAMP WITH TIME ZONE NULL;

CREATE INDEX idx_outbox_events_claimable
    ON outbox_events (claimed_at, occurred_at)
    WHERE published_at IS NULL;