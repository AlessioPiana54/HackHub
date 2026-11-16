# HackHub

<p align="center">
  <img src="docs/hackhub.webp" alt="HackHub - hackathon management platform" width="400">
</p>

![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.2-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

Platform for hackathon management (users, teams, registrations, submissions, invitations, support requests).

## Key Features

- **Authentication**: Registration and login with stateless JWT. Distinct roles: Organizer, Judge, Mentor, Teamless User, Team Leader, Team Member.
- **Hackathon Management**: Creation, automatic state progression (Pending → Open → In Progress → Evaluation → Award → Concluded) via scheduler.
- **Team Management**: Team creation, email invitations (any team member can invite), accept/reject/withdraw, leadership transfer, leave team.
- **Registrations**: Teams can register for hackathons during the open registration phase.
- **Submissions**: Registered teams can submit their project GitHub link during the In Progress phase.
- **Evaluations**: The assigned judge evaluates each submission once with a score (0–10) and written feedback, then closes the evaluation phase.
- **Leaderboard & Winner**: Standings by score; the organizer proclaims the winner, the prize is paid (mock) to the team leader and the hackathon is concluded.
- **Support Requests**: Teams can request mentorship with a proposed call (Google Meet/Webex).
- **Reports**: Mentors report teams to the organizer; team members see the reports about their own team.
- **PWA**: Installable as a native app, with offline support for previously visited content.

---

## System Architecture

```
                Browser
                  |
                  v
        +---------------------+
        |  Frontend (Angular) |
        |  SPA + Nginx proxy  |
        +----------+----------+
                   |
                   |  /api/*  (proxy → backend)
                   v
        +---------------------+          JDBC
        | Backend (Spring)    |--------------------+
        | REST API :8080      |                    |
        | Swagger/OpenAPI     |                    |
        +----------+----------+                    |
                   |                               |
                   v                               |
        +---------------------+                    |
        | PostgreSQL          |<-------------------+
        | db: hackhub         |
        +---------------------+
```

---

## Deployment Environments

| Environment | Requirements | URL |
| :--- | :--- | :--- |
| **Docker Compose** (local development) | Docker Desktop | `http://localhost:4200` |
| **Local without Docker** | Java 21, Node 22, PostgreSQL | `http://localhost:4200` |
| **Kubernetes / Minikube** (local) | Docker Desktop, Minikube, kubectl | `minikube service frontend -n hackhub` |

---

## Running with Docker Compose

The easiest way to start the full stack locally.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- `.env` file in the root directory (use `.env.example.env` as a template)

### Initial Setup

```bash
cp .env.example.env .env
```

Default values ready for local development:

```env
JWT_SECRET=change-this-key-in-production-minimum-64-characters-required!!
JWT_EXPIRATION=86400000
DB_URL=jdbc:postgresql://postgres:5432/hackhub
DB_USERNAME=hackhub
DB_PASSWORD=hackhub
```

In production, replace at least `JWT_SECRET` with a secure random string (minimum 64 characters).

> The frontend is available at `http://localhost:4200` (Compose maps host port 4200 → Nginx container port 8080).

> Hackathon dates are stored without a time zone: the backend container runs with `TZ=Europe/Rome`, so the scheduler compares them with Italian local time.

### Start/Stop

```bash
# Start the full stack (with image build)
docker compose up --build -d

# Container status
docker compose ps

# Stop and remove containers
docker compose down
```

---

## Local Development without Docker

Run services separately during development.

### Backend (Spring Boot)

Requires an active PostgreSQL instance (you can use `docker compose up postgres -d`).

```bash
cd Codice/app
./mvnw spring-boot:run
```

The backend will be available at `http://localhost:8080`.

### Frontend (Angular)

```bash
cd Codice/frontend
npm install
npm start
```

The frontend will be available at `http://localhost:4200` with automatic proxy to the backend (`proxy.conf.json`).

---

## Deploy on Kubernetes (Minikube)

Manifests are in [k8s/](k8s/) and allow running the app on local Kubernetes.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/) — `winget install Kubernetes.minikube`
- kubectl (included with Docker Desktop)

### Startup

```powershell
# 1. Start the cluster
minikube start --driver=docker

# 2. Point Docker at Minikube's registry
minikube docker-env | Invoke-Expression

# 3. Build images inside the cluster
docker build -t hackhub-backend:latest ./Codice/app
docker build -t hackhub-frontend:latest ./Codice/frontend

# 4. Create the secrets file (NOT included in the repository)
cp k8s/02-secrets.yaml.example k8s/02-secrets.yaml
# Edit k8s/02-secrets.yaml and set DB_PASSWORD and JWT_SECRET

# 5. Apply manifests
kubectl apply -f k8s/

# 6. Open the app in the browser
minikube service frontend -n hackhub
```

### Manifest Structure

```
k8s/
├── 01-namespace.yaml         # "hackhub" namespace
├── 02-secrets.yaml.example   # Credentials template (copy to 02-secrets.yaml and fill in; not applied by kubectl)
├── 02-secrets.yaml           # DB password and JWT secret — NOT in git
├── 03-configmap.yaml         # Configuration variables
├── 04-postgres.yaml          # PostgreSQL Deployment + PVC + Service
├── 05-backend.yaml           # Backend Deployment + Service
└── 06-frontend.yaml          # Frontend Deployment + NodePort Service
```

### Useful Commands

```powershell
# Pod status
kubectl get pods -n hackhub

# Logs for a pod
kubectl logs -n hackhub <pod-name>

# Stop the cluster
minikube stop

# Delete everything
kubectl delete namespace hackhub
```

---

## Running Tests

### Backend Tests

Unit tests (JUnit 5 + Mockito) cover the application services (Auth, Hackathon, Team, Invito, Sottomissione, RichiestaSupporto, Segnalazione, User), the `HackathonScheduler` state transitions and the link validation strategies.

```bash
cd Codice/app
./mvnw test
```

For the coverage report (JaCoCo):

```bash
./mvnw verify
```

Report available at `Codice/app/target/site/jacoco/index.html`.

### Frontend Tests

Unit tests cover core services, guards, interceptors, and directives using Karma + Jasmine.

```bash
cd Codice/frontend
npm test
```

---

## Swagger / OpenAPI

The backend exposes interactive API documentation.

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## Test Credentials

These users are automatically seeded by `DataSeeder` on startup if the database is empty. The password for all accounts is **`Test1234!`**.

| Name | Email | Role |
| :--- | :--- | :--- |
| Mario Rossi | `mario@hackhub.it` | ORGANIZZATORE |
| Luigi Verdi | `luigi@hackhub.it` | ORGANIZZATORE |
| Giovanni Bianchi | `giudice@hackhub.it` | GIUDICE |
| Paolo Gialli | `mentore@hackhub.it` | MENTORE |
| Francesca Viola | `utente1@hackhub.it` | UTENTE_SENZA_TEAM |
| Matteo Rosso | `utente2@hackhub.it` | UTENTE_SENZA_TEAM |

---

## Roles & Permissions Matrix

Roles are assigned automatically by the system based on user actions. `UTENTE_SENZA_TEAM` → `LEADER_TEAM` on team creation; → `MEMBRO_TEAM` on invitation acceptance; `LEADER_TEAM` → `MEMBRO_TEAM` on leadership transfer.

| Action | ORGANIZZATORE | GIUDICE | MENTORE | LEADER_TEAM | MEMBRO_TEAM | UTENTE_SENZA_TEAM |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| Create a hackathon | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Close the evaluation phase | ❌ | ✅ (assigned only) | ❌ | ❌ | ❌ | ❌ |
| Assign judges / mentors | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Declare winning team | ✅ (own hackathon) | ❌ | ❌ | ❌ | ❌ | ❌ |
| Evaluate a submission | ❌ | ✅ (assigned only) | ❌ | ❌ | ❌ | ❌ |
| Create a team | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Invite members to the team | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ |
| Withdraw a sent invitation | ❌ | ❌ | ❌ | ✅ | ✅ (own only) | ❌ |
| Accept / reject an invitation | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Transfer leadership | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Leave the team | ❌ | ❌ | ❌ | ✅ (only if sole member) | ✅ | ❌ |
| Register team for a hackathon | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Submit project (GitHub link) | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ |
| Request support from a mentor | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ |
| Respond to a support request | ❌ | ❌ | ✅ (assigned only) | ❌ | ❌ | ❌ |
| Send a report about a team | ❌ | ❌ | ✅ (assigned only) | ❌ | ❌ | ❌ |
| View the leaderboard | ✅ | ✅ | ✅* | ✅* | ✅* | ✅* |
| View own hackathons / teams | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

\* During the award phase the leaderboard is visible only to the organizer and the judge; once the hackathon is concluded it is public.

---

## Main REST Endpoints

Base path: `/api`

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`

### Users
- `GET /users/me`
- `PUT /users/me`
- `GET /users/by-role/{role}` — organizers only, `role ∈ {GIUDICE, MENTORE}`

### Hackathons
- `GET /hackathons`
- `GET /hackathons/{hackathonId}`
- `POST /hackathons`
- `GET /hackathons/my`
- `GET /hackathons/judge/my`
- `GET /hackathons/mentor/my`
- `GET /hackathons/organizer/my`
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

### Invitations
- `POST /invitations`
- `PATCH /invitations/{id}`
- `DELETE /invitations/{id}`
- `GET /invitations/received`
- `GET /invitations/sent`

### Submissions
- `POST /submissions`
- `PATCH /submissions/{id}`
- `PATCH /submissions/{id}/evaluation`
- `GET /submissions/my-submissions`
- `GET /submissions/hackathon/{idHackathon}` — organizer, judge and mentors of the hackathon

### Support Requests
- `POST /support-requests`
- `GET /support-requests?hackathonId=...`
- `PATCH /support-requests/{id}/call`
- `GET /support-requests/proposte-call?hackathonId=...&teamId=...`

### Reports
- `POST /segnalazioni`
- `GET /segnalazioni?hackathonId=...`

---

## Design & Architecture Decisions

HackHub adopts a **Clean Architecture** split into 4 layers:

1. **Core**: Business entities (POJOs) and fundamental interfaces. No dependencies on other layers.
2. **Application**: Business logic, services, and repository interfaces (IUnitOfWork).
3. **Infrastructure**: Technology implementations (JPA, Security, Database).
4. **Presentation**: REST API exposure via Spring Boot controllers.

### Patterns & Technologies

- **Unit of Work**: Coordinates multiple repositories in atomic transactions.
- **Builder Pattern**: Used for `Hackathon` entity creation (many required fields).
- **Strategy Pattern**: External link validation by host (GitHub for submissions, Google Meet/Webex for mentoring calls) — extendable without modifying existing logic.
- **JWT Stateless**: Stateless API for optimal horizontal scalability.
- **BCrypt**: Password hashing with built-in salt, resistant to brute-force and rainbow table attacks.
- **Angular PWA**: Service Worker for caching and offline support.
- **PwaService (SRP)**: "Add to Home Screen" installation logic encapsulated in a dedicated service.
- **Angular Lazy Loading**: Feature modules loaded on demand to optimise the initial bundle.

---

## 12-Factor App Compliance

| Factor | Status | Notes |
| :--- | :---: | :--- |
| I. Codebase | ✅ | Single GitHub repository for the entire project. |
| II. Dependencies | ✅ | Explicitly declared in `pom.xml` and `package.json`. |
| III. Config | ✅ | Configuration loaded from environment variables (`.env`, K8s ConfigMap/Secret). |
| IV. Backing services | ✅ | PostgreSQL treated as an external resource connected via JDBC. |
| V. Build, release, run | ✅ | CI/CD pipeline separates build (Docker images) from release and run. |
| VI. Processes | ✅ | Stateless backend thanks to JWT tokens. |
| VII. Port binding | ✅ | Services expose port 8080 (backend) and port 8080 (frontend Nginx). |
| VIII. Concurrency | ✅ | Horizontal scalability via Docker/K8s replicas. |
| IX. Disposability | ✅ | Fast startup and graceful shutdown with Alpine images. |
| X. Dev/prod parity | ✅ | Same Docker images used with Docker Compose and on Kubernetes. |
| XI. Logs | ✅ | Logs as event streams on stdout/stderr. |
| XII. Admin processes | ✅ | DataSeeder runs in the same operational environment. |
| XIII. API First | ✅ | REST APIs documented with Swagger/OpenAPI. |
| XIV. Telemetry | ✅ | Spring Boot Actuator for health checks and monitoring. |
| XV. Security | ✅ | JWT authentication and Spring Security on every endpoint. |

---

## Progressive Web App (PWA)

HackHub is installable as a native app on mobile and desktop devices.

- **Offline Support**: Previously visited sections of the app are accessible without a connection.
- **Install Banner**: Personalised installation prompt shown when criteria are met.
- **Smart Cache**: *Freshness* strategy — attempts the network first, falls back to cache.

### Testing the PWA Locally

The Service Worker is not active with `ng serve`. To test it:

```bash
cd Codice/frontend
npm run build -- --configuration production
npx http-server dist/frontend -p 4200
```

Open `http://localhost:4200` in Chrome and check `DevTools → Application → Service Workers`.

---

## Diagrams

### Deployment Diagram — Docker Compose (local)

```mermaid
graph TD
    User((Browser)) -->|localhost:4200| Frontend

    subgraph Docker Network - hackhub-network
        Frontend[Angular + Nginx :8080]
        Backend[Spring Boot :8080]
        DB[(PostgreSQL :5432)]
    end

    Frontend -->|/api/* proxy_pass| Backend
    Backend -->|JDBC| DB
```

### Deployment Diagram — Kubernetes / Minikube

```mermaid
graph TD
    User((Browser)) -->|NodePort 30080| Frontend[Pod: frontend Nginx]
    Frontend -->|ClusterIP :8080| Backend[Pod: backend Spring]
    Backend -->|ClusterIP :5432| DB[(Pod: postgres)]

    subgraph Namespace: hackhub
        Frontend
        Backend
        DB
    end
```

### Backend Architecture

```mermaid
graph LR
    subgraph Presentation
        C[Controllers]
        D[DTOs]
    end
    subgraph Application
        S[Services]
        U[IUnitOfWork]
    end
    subgraph Core
        E[Entities]
        I[Interfaces]
    end
    subgraph Infrastructure
        R[Repositories/JPA]
        SEC[Security/JWT]
    end

    Presentation --> Application
    Application --> Core
    Infrastructure --> Core
    Infrastructure -.-> Application
```

---

## CI/CD Pipeline

The project includes a CI/CD pipeline via **GitHub Actions**:

- **Continuous Integration**: On every push or PR to `main`, Maven (backend) and npm (frontend) builds and tests are executed.
- **Continuous Deployment**: On push to `main`, the pipeline builds Docker images and publishes them to the GitHub Container Registry (GHCR), tagged `latest` and with the commit SHA.

Configuration: [.github/workflows/ci.yml](.github/workflows/ci.yml)
