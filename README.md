# VetManager Portal

Progetto didattico per la gestione di una clinica veterinaria, composto da:

- frontend React/Vite servito da Nginx;
- API Spring Boot con autenticazione JWT;
- database PostgreSQL 17 e migrazioni Flyway;
- invio email e generazione di fatture e ricette in PDF.

## Architettura

L'ambiente Docker include tre servizi:

| Servizio | Funzione | Accesso dall'host |
| --- | --- | --- |
| `frontend` | Interfaccia web e proxy `/api` | `${FRONTEND_PORT:-8080}` |
| `backend` | API Spring Boot sulla porta 9020 | Solo interno |
| `database` | PostgreSQL 17 | Solo interno |

I servizi comunicano tramite la rete `vetmanager_application`. Database, fatture e ricette usano volumi separati.

## Avviso di sicurezza

Le credenziali presenti in precedenti commit devono essere considerate compromesse. Sostituisci subito password del database, credenziali email e segreto JWT. Conserva i nuovi valori solo nel file `.env`, escluso da Git, o in un gestore di segreti.

## Avvio rapido

Requisiti: Git e Docker con Compose v2.

```powershell
Copy-Item .env.example .env
```

Nel file `.env`, modifica almeno `POSTGRES_PASSWORD` e `JWT_SECRET`. Per generare una chiave JWT in Base64:

```powershell
$jwtBytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Fill($jwtBytes)
[Convert]::ToBase64String($jwtBytes)
```

Avvia l'applicazione:

```powershell
docker compose up --build -d
```

Apri <http://localhost:8080>, oppure la porta definita in `FRONTEND_PORT`.

## Verifica e gestione

```powershell
# Stato dei container
docker compose ps

# Health check di frontend e backend
Invoke-WebRequest http://localhost:8080/health
Invoke-RestMethod http://localhost:8080/api/health

# Log
docker compose logs --follow

# Arresto senza eliminare i dati
docker compose down
```

Per azzerare **solo il database locale**:

```powershell
docker compose down
docker volume rm vetmanager_database_data
docker compose up --build -d
```

Non usare `docker compose down --volumes` salvo quando vuoi eliminare anche fatture e ricette generate.

## Migrazioni del database

Flyway applica automaticamente le migrazioni presenti in:

```text
ProgettoSpring/LBP-App-Vet/src/main/resources/db/migration
```

Per ogni modifica crea un nuovo file versionato, ad esempio `V3__add_appointment_status.sql`. Non modificare migrazioni già applicate a database condivisi.

## Sviluppo locale

Requisiti aggiuntivi: Java 21, Node.js 24 con npm e PostgreSQL 17.

Backend:

```powershell
Set-Location ProgettoSpring\LBP-App-Vet
.\mvnw.cmd spring-boot:run
```

Le variabili `DB_*`, `JWT_SECRET`, `MAIL_*` e i percorsi di archiviazione devono essere configurati nell'ambiente. Il backend è disponibile su <http://localhost:9020>.

Frontend, in un altro terminale:

```powershell
Set-Location React\vetmanager-portal
npm ci
npm run dev
```

## Controlli di qualità

```powershell
# Backend
Set-Location ProgettoSpring\LBP-App-Vet
.\mvnw.cmd test

# Frontend
Set-Location React\vetmanager-portal
npm ci
npm run lint
npm run build
```

## Struttura del repository

```text
LearningByProject/
|-- compose.yml
|-- .env.example
|-- ProgettoSpring/LBP-App-Vet/    # Backend e migrazioni
|-- React/vetmanager-portal/       # Frontend e configurazione Nginx
|-- DatiExcel/                     # Dati di importazione e riferimento
|-- Documentazione/                # Documentazione del progetto
`-- Mockup/                        # Mockup iniziali dell'interfaccia
```

Il progetto è destinato all'apprendimento. Prima di un eventuale uso in produzione, verifica sicurezza, autorizzazioni, backup, email e monitoraggio.
