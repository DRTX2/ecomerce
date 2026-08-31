CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    metadata JSONB,
    occurred_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_audit_logs_actor_occurred_at ON audit_logs(actor_id, occurred_at DESC);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id, occurred_at DESC);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(200) NOT NULL,
    payload JSONB NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    available_at TIMESTAMPTZ NOT NULL,
    processed_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error TEXT
);

CREATE INDEX idx_outbox_events_pending ON outbox_events(available_at, occurred_at)
    WHERE processed_at IS NULL;
