# HackHub

HackHub è una piattaforma backend sviluppata in Java con Spring Boot per la gestione completa di Hackathon. Questo progetto è stato realizzato per il corso di Ingegneria del Software (IdS) presso l'Università di Camerino (Unicam).

## 📋 Descrizione

HackHub permette di gestire l'intero ciclo di vita di un hackathon, dalla creazione alla premiazione, includendo la gestione di team, partecipanti, giudici e mentori. Il sistema supporta diverse fasi (iscrizione, svolgimento, valutazione) e fornisce funzionalità per la sottomissione dei progetti e la loro valutazione.

## 🚀 Tecnologie Utilizzate

*   **Java 21**: Linguaggio di programmazione principale.
*   **Spring Boot**: Framework per lo sviluppo dell'applicazione backend.
*   **Spring Data JPA**: Per la persistenza dei dati.
*   **H2 Database**: Database in-memory per lo sviluppo e il testing rapido.
*   **Maven**: Strumento di build e gestione delle dipendenze.

## ✨ Funzionalità Principali

*   **Gestione Utenti**: Ruoli differenziati (Organizzatore, Partecipante, Giudice, Mentore, Visitatore).
*   **Gestione Hackathon**: Creazione e gestione degli stati dell'evento (Creato, Iscrizione Aperta, In Corso, Valutazione, Concluso).
*   **Gestione Team**: Creazione squadre, inviti, adesione e abbandono dei membri.
*   **Sottomissioni**: Caricamento dei progetti da parte dei team (supporto per link GitHub).
*   **Valutazioni**: Sistema di valutazione delle Sottomissioni da parte dei Giudici.
*   **Richieste di Supporto**: I team possono richiedere aiuto ai mentori (integrazione con link per call come Google Meet/Webex).
*   **Scheduler Automatico**: Avanzamento automatico delle fasi dell'hackathon basato sulle date.

## 🛠️ Architettura e Pattern

Il progetto segue un'architettura a livelli (Layered Architecture) con una separazione chiara tra:
*   **Presentation**: Controller REST.
*   **Application**: Logica di business e servizi.
*   **Core**: Entità del dominio.
*   **Infrastructure**: Implementazione della persistenza e utility (come il Sistema Esterno di Pagamento, il meccanismo di Hashing delle Password, ecc.).

Pattern progettuali utilizzati includono:
*   **Unit of Work**: Per gestire le transazioni e l'accesso ai dati in modo atomico.
*   **Builder**: Per la costruzione complessa di oggetti (es. entità Hackathon).
*   **Strategy**: Per la gestione e la validazione dei link esterni (es. link GitHub, link per call come Google Meet/Webex).

## 📦 Installazione e Avvio

### Prerequisiti
*   JDK 21 installato.
*   Maven (opzionale, incluso nel wrapper `mvnw`).

### Passaggi

1.  Clona il repository:
    ```bash
    git clone <url-repository>
    ```
2.  Naviga nella cartella del progetto:
    ```bash
    cd HackHub/Codice/app
    ```
3.  Avvia l'applicazione con Maven Wrapper:
    *   **Windows**:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   **Linux/Mac**:
        ```bash
        chmod +x mvnw
        ./mvnw spring-boot:run
        ```

L'applicazione si avvierà sulla porta `8080`.

## 📚 Documentazione API

Una volta avviata l'applicazione, puoi accedere alla documentazione interattiva delle API (Swagger UI) al seguente indirizzo:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🗄️ Database Console

Per ispezionare il database H2 in memoria:
*   URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
*   JDBC URL: `jdbc:h2:mem:testdb`
*   Username: `user`
*   Password: `pass`


## 🧪 Sequenza di Test Manuale

Di seguito è riportata una sequenza di operazioni per testare manualmente il flusso principale dell'applicazione.

### 1. Popolamento Database (SQL)

Esegui questi comandi SQL (es. nella H2 Console) per inserire utenti di test con ruoli diversi.
> **Importante**:
> La password per tutti questi utenti è `a`.

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

### 2. Flusso Operativo (Chiamate API)

> **Importante**: Dopo ogni login, copia il `token` ricevuto nella risposta e utilizzalo nell'header `Authorization` per le chiamate successive relative a quell'utente.
> Header: `Authorization: Bearer <TOKEN>`

#### A. Autenticazione (Login)

Recupera i token per i vari attori.

**Login Mentore (Paolo Gialli)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "paolo.gialli@mentor.com",
 "password": "a"
}
```

**Login Giudice (Giovanni Bianchi)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "giovanni.bianchi@judge.com",
 "password": "a"
}
```

**Login Organizzatore (Mario Rossi)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "mario.rossi@organizer.com",
 "password": "a"
}
```

**Login Utente (Francesca Viola - Utente 7)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "francesca.viola@user.com",
 "password": "a"
}
```

**Login Utente (Matteo Roso - Utente 8)**
`POST http://localhost:8080/api/auth/login`
```json
{
 "email": "matteo.roso@user.com",
 "password": "a"
}
```

#### B. Gestione Team

**1. Creazione Team (Utente 7)**
`POST http://localhost:8080/api/teams/crea`
*Header Auth: Token Utente 7*
```json
{
 "nomeTeam": "i Dinosauri"
}
```
*Nota: Copia `id` del team creato (es. `TEAM_ID`).*

**2. Invito Membro (da Utente 7 a Utente 8)**
`POST http://localhost:8080/api/inviti/invia`
*Header Auth: Token Utente 7*
```json
{
 "teamId": "TEAM_ID",
 "emailDestinatario": "matteo.roso@user.com"
}
```
*Nota: Copia `id` dell'invito dalla risposta (es. `INVITO_ID`).*

**3. Accettazione Invito (Utente 8)**
`POST http://localhost:8080/api/inviti/risposta`
*Header Auth: Token Utente 8*
```json
{
 "invitoId": "INVITO_ID",
 "accettato": true
}
```

#### C. Gestione Hackathon

**1. Creazione Hackathon (Organizzatore 1)**
`POST http://localhost:8080/api/hackathons/crea`
*Header Auth: Token Organizzatore 1*
*Nota: Assicurati che le date siano future rispetto al momento del test per permettere l'iscrizione.*
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
*Nota: Copia `id` dell'hackathon (es. `HACKATHON_ID`).*

**2. Iscrizione al Hackathon (Utente 7 - Leader Team)**
`POST http://localhost:8080/api/teams/TEAM_ID/iscrivi?hackathonId=HACKATHON_ID`
*Header Auth: Token Utente 7*

#### D. Svolgimento e Valutazione

**1. Sottomissione Progetto (Utente 7 - Leader Team)**
`POST http://localhost:8080/api/sottomissioni/invia`
*Header Auth: Token Utente 7*
*Nota: L'hackathon deve essere in fase "IN CORSO" (tra dataInizio e dataFine).*
```json
{
 "idHackathon": "HACKATHON_ID",
 "idTeam": "TEAM_ID",
 "linkProgetto": "https://github.com/mio-team/progetto/progetto",
 "descrizione": "Descrizione del nostro progetto rivoluzionario."
}
```
*Nota: Copia `id` della sottomissione (es. `SOTTOMISSIONE_ID`).*

**2. Valutazione (Giudice 3)**
`POST http://localhost:8080/api/sottomissioni/valuta`
*Header Auth: Token Giudice 3*
*Nota: L'hackathon deve essere in fase "VALUTAZIONE" (dopo dataFine).*
```json
{
 "idSottomissione": "SOTTOMISSIONE_ID",
 "voto": 8.5,
 "giudizio": "Ottimo lavoro, codice pulito ma documentazione carente."
}
```

**3. Termina Valutazione (Scelta Organizzatore o Automatica)**
`POST http://localhost:8080/api/hackathons/HACKATHON_ID/terminaValutazione`
*Header Auth: Token Organizzatore 1*

**4. Proclama Vincitore (Organizzatore 1)**
`POST http://localhost:8080/api/hackathons/HACKATHON_ID/vincitore?teamId=TEAM_ID`
*Header Auth: Token Organizzatore 1*

#### E. Supporto (Mentoring)

**1. Richiesta Supporto (Utente 7)**
`POST http://localhost:8080/api/supporto/crea`
*Header Auth: Token Utente 7*
```json
{
 "hackathonId": "HACKATHON_ID",
 "teamId": "TEAM_ID",
 "descrizione": "Abbiamo bisogno di aiuto con il pagamento"
}
```
*Nota: Copia `id` richiesta (es. `RICHIESTA_ID`).*

**2. Visualizza Richieste (Mentore 5)**
`GET http://localhost:8080/api/supporto/mentore?hackathonId=HACKATHON_ID`
*Header Auth: Token Mentore 5*

**3. Proponi Call (Mentore 5)**
`POST http://localhost:8080/api/supporto/proponi-call`
*Header Auth: Token Mentore 5*
```json
{
 "richiestaId": "RICHIESTA_ID",
 "linkCall": "https://meet.google.com/abc",
 "dataCall": "2027-02-16T15:00:00"
}
```

## 👥 Autori

* Luca Soricetti
* Alessio Pianaroli
* Davide Perruolo