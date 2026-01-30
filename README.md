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
    cd app
    ```
3.  Avvia l'applicazione con Maven Wrapper:
    *   **Windows**:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   **Linux/Mac**:
        ```bash
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

## 👥 Autori

Luca Soricetti
Alessio Pianaroli
Davide Perruolo