CREATE TABLE IF NOT EXISTS users (
    nickname TEXT PRIMARY KEY,
    nome TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'available',
    ping_ms INTEGER,
    online BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT check_status CHECK (status IN ('available', 'busy', 'away')),
    CONSTRAINT check_ping_positivo CHECK (ping_ms >= 0)
);