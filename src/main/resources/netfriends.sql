CREATE TABLE IF NOT EXISTS netfriends (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_nickname TEXT NOT NULL,
    netfriend TEXT NOT NULL,
    UNIQUE (user_nickname, netfriend),
    FOREIGN KEY (user_nickname) REFERENCES users(nickname) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (netfriend) REFERENCES users(nickname) ON DELETE CASCADE ON UPDATE CASCADE
);