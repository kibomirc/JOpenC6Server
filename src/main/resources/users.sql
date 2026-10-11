CREATE TABLE IF NOT EXISTS users (
    nickname TEXT PRIMARY KEY,
    email    TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    status   TEXT NOT NULL DEFAULT 'DISPONIBILE',
    ping_ms  INTEGER,
    online   BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT check_status CHECK (status IN ('DISPONIBILE', 'SOLO_NETFRIEND', 'IMPEGNATO')),
    CONSTRAINT check_ping_positivo CHECK (ping_ms >= 0)
);