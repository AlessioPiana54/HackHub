-- =============================================================================
-- V1 - Schema iniziale HackHub
-- Eseguita da Flyway al primo deploy su DB vuoto (profilo prod).
-- In dev il profilo usa ddl-auto=update e Flyway è disabilitato.
-- =============================================================================

-- ----------------------------------------------------------------------------
-- USERS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id       VARCHAR(36)  PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    cognome  VARCHAR(100) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ruolo    VARCHAR(50)  NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- ----------------------------------------------------------------------------
-- TEAMS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS teams (
    id             VARCHAR(36)  PRIMARY KEY,
    nome_team      VARCHAR(255) NOT NULL,
    leader_id      VARCHAR(36)  UNIQUE REFERENCES users(id),
    data_creazione TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_teams_leader ON teams (leader_id);

-- team_membri (ManyToMany Team ↔ User)
CREATE TABLE IF NOT EXISTS team_membri (
    team_id VARCHAR(36) NOT NULL REFERENCES teams(id),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    PRIMARY KEY (team_id, user_id)
);

-- ----------------------------------------------------------------------------
-- HACKATHONS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hackathons (
    id                   VARCHAR(36)    PRIMARY KEY,
    nome                 VARCHAR(255)   NOT NULL,
    regolamento          TEXT           NOT NULL,
    inizio_iscrizioni    TIMESTAMP      NOT NULL,
    scadenza_iscrizioni  TIMESTAMP      NOT NULL,
    data_inizio          TIMESTAMP      NOT NULL,
    data_fine            TIMESTAMP      NOT NULL,
    luogo                VARCHAR(255)   NOT NULL,
    logo_url             VARCHAR(500),
    premio_in_denaro     NUMERIC(10,2)  NOT NULL CHECK (premio_in_denaro >= 0),
    data_creazione       TIMESTAMP      NOT NULL,
    stato                VARCHAR(50)    NOT NULL,
    organizzatore_id     VARCHAR(36)    REFERENCES users(id),
    giudice_id           VARCHAR(36)    REFERENCES users(id),
    vincitore_id         VARCHAR(36)    REFERENCES teams(id),
    version              BIGINT         DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_hackathons_stato         ON hackathons (stato);
CREATE INDEX IF NOT EXISTS idx_hackathons_organizzatore ON hackathons (organizzatore_id);

-- hackathon_mentori (ManyToMany Hackathon ↔ User)
CREATE TABLE IF NOT EXISTS hackathon_mentori (
    hackathon_id VARCHAR(36) NOT NULL REFERENCES hackathons(id),
    user_id      VARCHAR(36) NOT NULL REFERENCES users(id),
    PRIMARY KEY (hackathon_id, user_id)
);

-- ----------------------------------------------------------------------------
-- PARTECIPAZIONI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS partecipazioni (
    id             VARCHAR(36) PRIMARY KEY,
    team_id        VARCHAR(36) NOT NULL REFERENCES teams(id),
    hackathon_id   VARCHAR(36) NOT NULL REFERENCES hackathons(id),
    data_iscrizione TIMESTAMP  NOT NULL,
    CONSTRAINT uq_partecipazione_team_hackathon UNIQUE (team_id, hackathon_id)
);

CREATE INDEX IF NOT EXISTS idx_partecipazioni_hackathon ON partecipazioni (hackathon_id);
CREATE INDEX IF NOT EXISTS idx_partecipazioni_team      ON partecipazioni (team_id);

-- ----------------------------------------------------------------------------
-- SOTTOMISSIONI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sottomissioni (
    id                VARCHAR(36)  PRIMARY KEY,
    partecipazione_id VARCHAR(36)  NOT NULL UNIQUE REFERENCES partecipazioni(id),
    mittente_id       VARCHAR(36)  NOT NULL REFERENCES users(id),
    link_progetto     VARCHAR(500) NOT NULL,
    descrizione       TEXT,
    data_sottomissione TIMESTAMP   NOT NULL,
    CONSTRAINT uq_sottomissione_partecipazione UNIQUE (partecipazione_id)
);

CREATE INDEX IF NOT EXISTS idx_sottomissioni_mittente ON sottomissioni (mittente_id);

-- ----------------------------------------------------------------------------
-- VALUTAZIONI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS valutazioni (
    id               VARCHAR(36) PRIMARY KEY,
    sottomissione_id VARCHAR(36) NOT NULL UNIQUE REFERENCES sottomissioni(id),
    giudice_id       VARCHAR(36) NOT NULL REFERENCES users(id),
    giudizio         TEXT        NOT NULL,
    voto             NUMERIC(4,2) NOT NULL CHECK (voto >= 0 AND voto <= 10),
    CONSTRAINT uq_valutazione_sottomissione UNIQUE (sottomissione_id)
);

CREATE INDEX IF NOT EXISTS idx_valutazioni_giudice ON valutazioni (giudice_id);

-- ----------------------------------------------------------------------------
-- INVITI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS inviti (
    id              VARCHAR(36) PRIMARY KEY,
    team_id         VARCHAR(36) NOT NULL REFERENCES teams(id),
    destinatario_id VARCHAR(36) NOT NULL REFERENCES users(id),
    mittente_id     VARCHAR(36) NOT NULL REFERENCES users(id),
    data_invito     TIMESTAMP   NOT NULL,
    CONSTRAINT uq_invito_team_destinatario UNIQUE (team_id, destinatario_id)
);

CREATE INDEX IF NOT EXISTS idx_inviti_destinatario ON inviti (destinatario_id);
CREATE INDEX IF NOT EXISTS idx_inviti_team         ON inviti (team_id);

-- ----------------------------------------------------------------------------
-- RICHIESTE SUPPORTO
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS richieste_supporto (
    id                VARCHAR(36) PRIMARY KEY,
    partecipazione_id VARCHAR(36) NOT NULL REFERENCES partecipazioni(id),
    richiedente_id    VARCHAR(36) NOT NULL REFERENCES users(id),
    descrizione       TEXT        NOT NULL,
    data_richiesta    TIMESTAMP   NOT NULL,
    link_call         VARCHAR(500),
    data_call         TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- SEGNALAZIONI
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS segnalazioni (
    id                VARCHAR(36) PRIMARY KEY,
    partecipazione_id VARCHAR(36) NOT NULL REFERENCES partecipazioni(id),
    mentore_id        VARCHAR(36) NOT NULL REFERENCES users(id),
    descrizione       TEXT        NOT NULL,
    data_segnalazione TIMESTAMP   NOT NULL
);
