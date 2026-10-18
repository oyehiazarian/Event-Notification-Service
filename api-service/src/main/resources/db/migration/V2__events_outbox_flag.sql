-- Rows that exist before this migration were already published by the old scheduler.
ALTER TABLE events ADD COLUMN IF NOT EXISTS sent BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE events ALTER COLUMN sent SET DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_events_pending ON events (id) WHERE sent = FALSE;
CREATE INDEX IF NOT EXISTS idx_events_user ON events (user_id);

UPDATE usersecurity SET role = upper(role);
