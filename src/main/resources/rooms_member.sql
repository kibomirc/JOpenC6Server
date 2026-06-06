CREATE TABLE IF NOT EXISTS room_members (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    roomName TEXT NOT NULL,
    nickname TEXT NOT NULL,
    UNIQUE(roomName, nickname),
    FOREIGN KEY (roomName) REFERENCES rooms(roomName) ON DELETE CASCADE
);