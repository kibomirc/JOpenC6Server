CREATE TABLE IF NOT EXISTS users (
    nickname TEXT PRIMARY KEY,
    status TEXT NOT NULL DEFAULT 'available',
    ping_ms INTEGER,

    CONSTRAINT check_status CHECK (status IN ('available', 'busy', 'away')),
    CONSTRAINT check_ping_positivo CHECK (ping_ms >= 0)
);