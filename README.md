# HackHub

Portale per la gestione di hackathon (utenti, team, iscrizioni, sottomissioni, inviti, richieste di supporto).

## Architettura del sistema (overview)

```
                Browser
                  |
                  |  http://localhost:4200
                  v
        +---------------------+
        |  Frontend (Angular) |
        |  SPA + proxy /api   |
        +----------+----------+
                   |
                   |  /api/*  (proxy -> backend)
                   v
        +---------------------+          JDBC
        | Backend (Spring)    |--------------------+
        | REST API :8080      |                    |
        | Swagger/OpenAPI     |                    |
        +----------+----------+                    |
                   |                               |
                   | 5432                          |
                   v                               |
        +---------------------+                    |
        | PostgreSQL          |<-------------------+
        | db: hackhub         |
        +---------------------+
```

## Avvio con Docker Compose (DB)

Nel repository è presente un `docker-compose.yml` che avvia PostgreSQL.

### Prerequisiti

- Docker Desktop

### Start/Stop

```bash
docker compose up -d
docker compose ps
docker compose down
```

### Credenziali DB (dev)

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `hackhub`
- **Username**: `hackhub`
- **Password**: `hackhub`

> Il backend supporta variabili d’ambiente: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (vedi `Codice/app/src/main/resources/application.properties`).

## Avvio Backend (Spring Boot)

Il backend gira di default su **porta 8080**.

Da PowerShell:

```bash
cd Codice/app
./mvnw spring-boot:run
```

Oppure (se usi Maven installato):

```bash
cd Codice/app
mvn spring-boot:run
```

## Avvio Frontend (Angular)

Il frontend gira su **porta 4200** ed è configurato con proxy verso il backend:

- `Codice/frontend/proxy.conf.json` inoltra `http://localhost:4200/api/*` a `http://localhost:8080/api/*`

```bash
cd Codice/frontend
npm install
npm start
```

## Swagger / OpenAPI

Il backend include `springdoc-openapi`.

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Esempio credenziali (da registrare)

- **Email**: `test@example.com`
- **Password**: `Test1234!`

Endpoint:
- `POST /api/auth/register`
- `POST /api/auth/login`

## Endpoint REST principali (nomenclatura attuale)

Base path: `http://localhost:8080/api`

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`

### Users

- `GET /users/me`
- `PUT /users/me`
- `GET /users/by-role/{ruolo}` dove `ruolo ∈ {ORGANIZZATORE, GIUDICE, MENTORE}`

### Hackathons

- `GET /hackathons` (pubblici)
- `GET /hackathons/{hackathonId}`
- `POST /hackathons` (creazione)
- `GET /hackathons/my`
- `GET /hackathons/judge/my`
- `GET /hackathons/mentor/my`
- `PATCH /hackathons/{hackathonId}/status`
- `GET /hackathons/{hackathonId}/classifica`
- `GET /hackathons/{hackathonId}/participants`
- `POST /hackathons/{hackathonId}/join?teamId=...`
- `POST /hackathons/{hackathonId}/winner?teamId=...`

### Teams

- `POST /teams`
- `PUT /teams/{teamId}`
- `DELETE /teams/{teamId}/members/me`
- `PATCH /teams/{teamId}/leader/{newLeaderId}`
- `GET /teams/my-teams`
- `GET /teams/{teamId}`
- `POST /teams/cleanup`

### Invitations (inviti)

- `POST /invitations`
- `PATCH /invitations/{id}`
- `GET /invitations/received`
- `GET /invitations/sent`

### Submissions (sottomissioni)

- `POST /submissions`
- `PATCH /submissions/{id}`
- `PATCH /submissions/{id}/evaluation`
- `GET /submissions/my-submissions`
- `GET /submissions/hackathon/{idHackathon}`

### Support requests (richieste supporto)

- `POST /support-requests`
- `GET /support-requests?hackathonId=...`
- `PATCH /support-requests/{id}/call`
- `GET /support-requests/proposte-call?hackathonId=...&teamId=...`

### Segnalazioni

- `POST /segnalazioni`
- `GET /segnalazioni?hackathonId=...`

