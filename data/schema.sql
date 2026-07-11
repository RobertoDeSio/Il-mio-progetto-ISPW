-- Schema database Weekender
-- SQLite

-- Tabella utenti
CREATE TABLE IF NOT EXISTS utenti (
                                      email           TEXT PRIMARY KEY,
                                      nome            TEXT NOT NULL,
                                      password        TEXT NOT NULL,
                                      ruolo           TEXT NOT NULL CHECK (ruolo IN ('CLIENTE', 'ORGANIZZATORE')),
                                      nome_azienda    TEXT,
                                      descrizione     TEXT
);

-- Tabella eventi
CREATE TABLE IF NOT EXISTS eventi (
                                      id              TEXT PRIMARY KEY,
                                      nome            TEXT NOT NULL,
                                      descrizione     TEXT,
                                      data            TEXT NOT NULL,
                                      luogo           TEXT NOT NULL,
                                      posti_totali    INTEGER NOT NULL,
                                      posti_occupati  INTEGER NOT NULL DEFAULT 0,
                                      prezzo          REAL NOT NULL,
                                      categoria       TEXT NOT NULL CHECK (categoria IN ('MOTO', 'BOAT', 'TREKKING'))
);

-- Tabella prenotazioni
CREATE TABLE IF NOT EXISTS prenotazioni (
                                            id                    TEXT PRIMARY KEY,
                                            evento_id             TEXT NOT NULL,
                                            cliente_email         TEXT NOT NULL,
                                            numero_partecipanti   INTEGER NOT NULL DEFAULT 1,
                                            note                  TEXT,
                                            stato                 TEXT NOT NULL DEFAULT 'IN_ATTESA'
                                                CHECK (stato IN ('IN_ATTESA', 'APPROVATA', 'RIFIUTATA', 'CONFERMATA'))
);

-- Dati iniziali: i 3 eventi presettati
INSERT OR IGNORE INTO eventi (id, nome, descrizione, data, luogo, posti_totali, posti_occupati, prezzo, categoria)
VALUES
    ('1', 'Giro in Moto sulle Dolomiti', 'Un tour mozzafiato tra i passi più belli delle Dolomiti.', '15 giugno 2026', 'Bolzano, IT', 10, 4, 45.0, 'MOTO'),
    ('2', 'Giornata in Barca a Vela', 'Una giornata indimenticabile in barca a vela.', '20 luglio 2026', 'Portofino, IT', 6, 0, 120.0, 'BOAT'),
    ('3', 'Trekking Monte Bianco', 'Escursione guidata sul Monte Bianco.', '10 agosto 2026', 'Courmayeur, IT', 15, 8, 30.0, 'TREKKING');

-- Dati iniziali: utenti dummy per il testing
INSERT OR IGNORE INTO utenti (nome, email, password, ruolo, nome_azienda, descrizione)
VALUES
    ('Mario Rossi',   'cliente@test.it', 'cliente123', 'CLIENTE',       NULL,            NULL),
    ('Laura Bianchi', 'org@test.it',     'org123',     'ORGANIZZATORE', 'Weekender Srl', 'Organizzatore di eventi outdoor');