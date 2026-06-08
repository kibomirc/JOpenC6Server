CREATE TABLE IF NOT EXISTS rooms (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    roomName      TEXT    NOT NULL UNIQUE,
    description   TEXT,
    ownerNickname TEXT    NOT NULL,
    type          TEXT    NOT NULL CHECK(type IN (
        'PRIVATE_USER_ROOM',
        'PUBLIC_USER_ROOM',
        'PRIVATE_SERVER_ROOM',
        'PUBLIC_SERVER_ROOM',
        'PRIVATE_USER_ROOM_ONLY_READER',
        'PUBLIC_USER_ROOM_ONLY_READER',
        'PRIVATE_SERVER_ROOM_ONLY_READER',
        'PUBLIC_SERVER_ROOM_ONLY_READER'
    )),
    eta                     TEXT,
    genere                  TEXT,
    orientamento            TEXT,
    occupazione             TEXT,
    area_geografica         TEXT,
    regione_provincia       TEXT,
    hobby                   TEXT,
    sport                   TEXT,
    genere_musicale         TEXT,
    genere_cinematografico  TEXT,
    comunita_virtuale       TEXT,
    odi_cordiali            TEXT
);