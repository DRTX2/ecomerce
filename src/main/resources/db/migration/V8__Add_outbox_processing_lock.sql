ALTER TABLE outbox_events ADD COLUMN locked_until TIMESTAMPTZ;

CREATE INDEX idx_outbox_events_claimable ON outbox_events(available_at, locked_until)
    WHERE processed_at IS NULL;
