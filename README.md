# HackHub

HackHub is a backend platform developed in Java with Spring Boot for the complete management of Hackathons. This project was developed for the Software Engineering (IdS) course at the University of Camerino (Unicam).

## Description

HackHub allows managing the entire lifecycle of a hackathon, from creation to awarding, including the management of teams, participants, judges, and mentors. The system supports various phases (registration, ongoing, evaluation) and provides features for project submission and evaluation.

## Technologies Used

*   **Java 21**: Main programming language.
*   **Spring Boot**: Framework for backend application development.
*   **Spring Data JPA**: For data persistence.
*   **H2 Database**: In-memory database for rapid development and testing.
*   **Maven**: Build and dependency management tool.

## Main Features

*   **User Management**: Differentiated roles (Organizer, Participant, Judge, Mentor, Visitor).
*   **Hackathon Management**: Creation and management of event states (Created, Registration Open, On Going, Evaluation, Concluded).
*   **Team Management**: Team creation, invitations, member joining and leaving.
*   **Submissions**: Project upload by teams (support for GitHub links).
*   **Evaluations**: Submission evaluation system by Judges.
*   **Support Requests**: Teams can request help from mentors (integration with call links like Google Meet/Webex).
*   **Automatic Scheduler**: Automatic advancement of hackathon phases based on dates.

## Architecture and Patterns

The project follows a Layered Architecture with a clear separation between:
*   **Presentation**: REST Controllers.
*   **Application**: Business logic and services.
*   **Core**: Domain entities.
*   **Infrastructure**: Persistence implementation and utilities (such as External Payment System, Password Hashing mechanism, etc.).

Design patterns used include:
*   **Unit of Work**: To manage transactions and data access atomically.
*   **Builder**: For complex object construction (e.g., Hackathon entity).
*   **Strategy**: For managing and validating external links (e.g., GitHub links, call links like Google Meet/Webex).

## Installation and Startup

### Prerequisites
*   JDK 21 installed.
*   Maven (optional, included in `mvnw` wrapper).

### Steps

1.  Clone the repository:
    ```bash
    git clone <url-repository>
    ```
2.  Navigate to the project folder:
    ```bash
    cd HackHub/Codice/app
    ```
3.  Start the application with Maven Wrapper:
    *   **Windows**:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   **Linux/Mac**:
        ```bash
        chmod +x mvnw
        ./mvnw spring-boot:run
        ```

The application will start on port `8080`.

## API Documentation

Once the application is started, you can access the interactive API documentation (Swagger UI) at the following address:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Database Console

To inspect the H2 in-memory database:
*   URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
*   JDBC URL: `jdbc:h2:mem:testdb`
*   Username: `user`
*   Password: `pass`


## Manual Test Sequence

Below is a sequence of operations to manually test the application's main flow.

### 1. Database Population (SQL)

Execute these SQL commands (e.g., in the H2 Console) to insert test users with different roles.
> **Important**:
> The password for all these users is `a`.

```sql
-- Insert 2 Organizers
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('1', 'Mario', 'Rossi', 'mario.rossi@organizer.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'ORGANIZZATORE');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('2', 'Luigi', 'Verdi', 'luigi.verdi@organizer.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'ORGANIZZATORE');

-- Insert 2 Judges
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('3', 'Giovanni', 'Bianchi', 'giovanni.bianchi@judge.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'GIUDICE');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('4', 'Anna', 'Neri', 'anna.neri@judge.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'GIUDICE');

-- Insert 2 Mentors
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('5', 'Paolo', 'Gialli', 'paolo.gialli@mentor.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'MENTORE');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('6', 'Laura', 'Blu', 'laura.blu@mentor.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'MENTORE');

-- Insert 4 Users without Team
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('7', 'Francesca', 'Viola', 'francesca.viola@user.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'UTENTE_SENZA_TEAM');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('8', 'Matteo', 'Roso', 'matteo.roso@user.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'UTENTE_SENZA_TEAM');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('9', 'Sara', 'Arancio', 'sara.arancio@user.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'UTENTE_SENZA_TEAM');
INSERT INTO users (id, nome, cognome, email, password, ruolo) VALUES ('10', 'Luca', 'Marrone', 'luca.marrone@user.com', 'ypeBEsobvcr6wjGzmiPcTaeG7/gUfE5yuYB3ha/uSLs=', 'UTENTE_SENZA_TEAM');
```

### 2. Operational Flow (API Calls)

> **Important**: After each login, copy the `token` received in the response and use it in the `Authorization` header for subsequent calls related to that user.
> Header: `Authorization: Bearer <TOKEN>`

#### A. Authentication (Login)

Retrieve tokens for the various actors.

**Mentor Login (Paolo Gialli)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "paolo.gialli@mentor.com",
 "password": "a"
}
```

**Judge Login (Giovanni Bianchi)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "giovanni.bianchi@judge.com",
 "password": "a"
}
```

**Organizer Login (Mario Rossi)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "mario.rossi@organizer.com",
 "password": "a"
}
```

**User Login (Francesca Viola - User 7)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "francesca.viola@user.com",
 "password": "a"
}
```

**User Login (Matteo Roso - User 8)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "matteo.roso@user.com",
 "password": "a"
}
```

#### B. Team Management

**1. Create Team (User 7)**
`POST http://localhost:8080/api/teams/crea`
*Header Auth: User Token 7*
```json
{
 "nomeTeam": "i Dinosauri"
}
```
*Note: Copy created team `id` (e.g., `TEAM_ID`).*

**2. Invite Member (from User 7 to User 8)**
`POST http://localhost:8080/api/inviti/invia`
*Header Auth: User Token 7*
```json
{
 "teamId": "TEAM_ID",
 "emailDestinatario": "matteo.roso@user.com"
}
```
*Note: Copy invitation `id` from response (e.g., `INVITATION_ID`).*

**3. Accept Invitation (User 8)**
`POST http://localhost:8080/api/inviti/risposta`
*Header Auth: User Token 8*
```json
{
 "invitoId": "INVITATION_ID",
 "accettato": true
}
```

#### C. Hackathon Management

**1. Create Hackathon (Organizer 1)**
`POST http://localhost:8080/api/hackathons/crea`
*Header Auth: Organizer Token 1*
*Note: Ensure dates are in the future relative to the test time to allow registration.*
```json
{
 "nome": "Hackathon 2026",
 "regolamento": "Regolamento ufficiale...",
 "inizioIscrizioni": "2026-02-10T23:22:00",
 "scadenzaIscrizioni": "2026-02-10T23:23:00",
 "dataInizio": "2026-02-10T23:24:00",
 "dataFine": "2026-02-10T23:30:00",
 "luogo": "Roma / Online",
 "premioInDenaro": 1000.0,
 "idGiudice": "3",
 "idMentori": ["5"]
}
```
*Note: Copy hackathon `id` (e.g., `HACKATHON_ID`).*

**2. Register for Hackathon (User 7 - Team Leader)**
`POST http://localhost:8080/api/teams/TEAM_ID/iscrivi?hackathonId=HACKATHON_ID`
*Header Auth: User Token 7*

#### D. Execution and Evaluation

**1. Project Submission (User 7 - Team Leader)**
`POST http://localhost:8080/api/sottomissioni/invia`
*Header Auth: User Token 7*
*Note: The hackathon must be in "IN CORSO" (On Going) phase (between dataInizio and dataFine).*
```json
{
 "idHackathon": "HACKATHON_ID",
 "idTeam": "TEAM_ID",
 "linkProgetto": "https://github.com/mio-team/progetto/progetto",
 "descrizione": "Descrizione del nostro progetto rivoluzionario."
}
```
*Note: Copy submission `id` (e.g., `SUBMISSION_ID`).*

**2. Evaluation (Judge 3)**
`POST http://localhost:8080/api/sottomissioni/valuta`
*Header Auth: Judge Token 3*
*Note: The hackathon must be in "VALUTAZIONE" (Evaluation) phase (after dataFine).*
```json
{
 "idSottomissione": "SUBMISSION_ID",
 "voto": 8.5,
 "giudizio": "Ottimo lavoro, codice pulito ma documentazione carente."
}
```

**3. End Evaluation (Organizer Choice or Automatic)**
`POST http://localhost:8080/api/hackathons/HACKATHON_ID/terminaValutazione`
*Header Auth: Organizer Token 1*

**4. Proclaim Winner (Organizer 1)**
`POST http://localhost:8080/api/hackathons/HACKATHON_ID/vincitore?teamId=TEAM_ID`
*Header Auth: Organizer Token 1*

#### E. Support (Mentoring)

**1. Request Support (User 7)**
`POST http://localhost:8080/api/supporto/crea`
*Header Auth: User Token 7*
```json
{
 "hackathonId": "HACKATHON_ID",
 "teamId": "TEAM_ID",
 "descrizione": "Abbiamo bisogno di aiuto con il pagamento"
}
```
*Note: Copy request `id` (e.g., `REQUEST_ID`).*

**2. View Requests (Mentor 5)**
`GET http://localhost:8080/api/supporto/mentore?hackathonId=HACKATHON_ID`
*Header Auth: Mentor Token 5*

**3. Propose Call (Mentor 5)**
`POST http://localhost:8080/api/supporto/proponi-call`
*Header Auth: Mentor Token 5*
```json
{
 "richiestaId": "REQUEST_ID",
 "linkCall": "https://meet.google.com/abc",
 "dataCall": "2027-02-16T15:00:00"
}
```

## Authors

* Luca Soricetti
* Alessio Pianaroli
* Davide Perruolo