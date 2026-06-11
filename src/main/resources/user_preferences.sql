CREATE TABLE IF NOT EXISTS user_preferences (
    nickname    TEXT     NOT NULL REFERENCES users(nickname) ON DELETE CASCADE,
    pref_index  SMALLINT NOT NULL,   -- serve per i vincoli
    slot        SMALLINT NOT NULL,
    pref_val    TEXT     NOT NULL,   -- es. 'GENERE_MUSICALE_JAZZ'

    PRIMARY KEY (nickname, pref_index, slot),
    CONSTRAINT check_slot CHECK (slot BETWEEN 1 AND 3),
    CONSTRAINT check_occupazione_singola CHECK (pref_index <> 4 OR slot = 1),
    CONSTRAINT uq_no_duplicati UNIQUE (nickname, pref_index, pref_val)
);