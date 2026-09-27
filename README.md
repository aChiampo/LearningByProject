# VetManager Portal

VetManager Portal is a teaching project for managing a veterinary clinic. It combines a React/Vite client, a Spring Boot API, PostgreSQL, Flyway database migrations, JWT authentication, email integration, and PDF generation.

The containerized development environment has exactly three application services:

| Service | Purpose | Host exposure |
| --- | --- | --- |
| `frontend` | Nginx serves the React build and proxies `/api` | `${FRONTEND_PORT:-8080}` |
| `backend` | Spring Boot API on port 9020 | Internal only |
| `database` | PostgreSQL 17.10 | Internal only |

All services share the isolated `vetmanager_application` bridge network. PostgreSQL data, generated invoices, and generated prescriptions use separate named volumes.

## Security warning

Database and email credentials were committed to this repository before the environment-variable migration. Treat those credentials as compromised:

1. Rotate the database password and email/application password immediately.
2. Revoke old credentials where the provider supports revocation.
3. Keep replacement values only in the ignored `.env` file or a proper secret manager.
4. Coordinate a Git history rewrite if the repository has been shared or pushed. Removing a value from the current file does not remove it from existing commits, clones, forks, or caches.

Never place real database, email, or JWT secrets in `.env.example`, `compose.yml`, `application.yml`, documentation, or commits.

## Prerequisites

- Docker Desktop or Docker Engine with Docker Compose v2
- Git
- A free host port for the frontend; port 8080 is the default
- For non-Docker development: Java 21, Node.js 24 with npm, and PostgreSQL 17

Check the container tooling:

```powershell
docker version
docker compose version
```

## Environment setup

From the repository root, create the local environment file:

```powershell
Copy-Item .env.example .env
```

The supplied values are development placeholders, not production secrets. At minimum, replace `POSTGRES_PASSWORD` and `JWT_SECRET`. A Base64-encoded JWT key can be generated in PowerShell without disabling certificate or TLS verification:

```powershell
$jwtBytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Fill($jwtBytes)
[Convert]::ToBase64String($jwtBytes)
```

Paste the output into `JWT_SECRET` in `.env`. Configure the `MAIL_*` entries only when email delivery is needed. The example SMTP hostname uses the reserved `.invalid` domain and cannot send mail. SMTP authentication and STARTTLS remain enabled by default.

The real `.env` is intentionally ignored. Confirm this with:

```powershell
git check-ignore -v .env
```

## Build and startup

Build all images and start the complete application:

```powershell
docker compose up --build -d
```

Open <http://localhost:8080> when `FRONTEND_PORT` has its default value. If the value is changed, use that port instead.

Only the frontend is published. Browser requests use relative `/api` URLs, and Nginx forwards them to `backend:9020` over the Compose network. The browser never needs a backend container hostname or a published backend port.

## Health checks

Show all container and health states:

```powershell
docker compose ps
```

Check Nginx from the host:

```powershell
Invoke-WebRequest http://localhost:8080/health
```

Check Spring Boot through the same public Nginx entry point:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

The second request is proxied to Spring Boot's readiness health group and should return `status` equal to `UP`. Internal checks can also be run directly inside their containers:

```powershell
docker compose exec backend wget -qO- http://127.0.0.1:9020/actuator/health/readiness
docker compose exec database pg_isready -U $env:POSTGRES_USER -d $env:POSTGRES_DB
```

If the PowerShell environment does not contain the database variables, substitute their values from the local `.env` file.

## Logs

Follow all logs:

```powershell
docker compose logs --follow
```

Follow one service or inspect recent messages:

```powershell
docker compose logs --follow backend
docker compose logs --tail 100 database
docker compose logs --tail 100 frontend
```

Press `Ctrl+C` to stop following logs; this does not stop the containers.

## Flyway behavior

Flyway runs automatically during Spring Boot startup. The backend connects to:

```text
jdbc:postgresql://database:5432/<POSTGRES_DB>
```

On a fresh database, Flyway executes the versioned scripts from:

```text
ProgettoSpring/LBP-App-Vet/src/main/resources/db/migration
```

Migration `V1` creates the application schema and `V2` inserts the required roles. Flyway records applied versions and checksums in `flyway_schema_history`. Hibernate uses `ddl-auto: validate`, so it validates the migrated schema instead of creating or modifying it.

Inspect the migration history:

```powershell
docker compose exec database psql -U vetmanager -d vetmanager -c 'SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;'
```

Replace the user and database values when the local `.env` uses different names. Previously successful versioned migrations are validated at restart and are not executed again.

## Creating later migrations

Add each schema change as a new SQL file in the Flyway migration directory. Use the next unused version and a descriptive name:

```text
V3__add_appointment_reminder_status.sql
V4__index_visits_by_status.sql
```

Rules:

- Never edit a migration that has already been applied to a shared database.
- Put forward-only schema or reference-data changes in the new file.
- Review SQL for compatibility with PostgreSQL 17.
- Rebuild and start the backend; Flyway will apply only pending migrations.
- Commit the migration together with the application code that requires it.

## Shutdown without deleting data

Stop and remove the application containers and network while retaining all named volumes:

```powershell
docker compose down
```

Alternatively, keep the containers and only stop them:

```powershell
docker compose stop
```

Restart stopped containers with `docker compose start`, or recreate them with `docker compose up --build -d`. PostgreSQL data, invoices, and prescriptions remain in their named volumes.

## Explicit database reset

The following operation permanently deletes the local PostgreSQL data while preserving the invoice and prescription volumes:

```powershell
docker compose down
docker volume rm vetmanager_database_data
docker compose up --build -d
```

Run it only when an intentional fresh database is required. Flyway will recreate the empty database schema during backend startup.

Do not use `docker compose down --volumes` unless all PostgreSQL data and all generated invoices and prescriptions are intentionally being discarded. It is not part of routine shutdown or verification.

## Local non-Docker development

### Backend

Start a PostgreSQL 17 instance and create the configured database and user. Then set the required variables for the current PowerShell session:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/vetmanager'
$env:DB_USERNAME = 'vetmanager'
$env:DB_PASSWORD = '<local-password>'
$env:JWT_SECRET = '<base64-encoded-key>'
$env:MAIL_HOST = 'smtp.example.invalid'
$env:MAIL_PORT = '587'
$env:MAIL_USERNAME = 'not-configured'
$env:MAIL_PASSWORD = 'not-configured'
$env:MAIL_FROM = 'noreply@example.invalid'
$env:MAIL_FIRST_APPOINTMENT_RECIPIENTS = 'noreply@example.invalid'
$env:INVOICE_STORAGE_PATH = 'storage/fatture'
$env:PRESCRIPTION_STORAGE_PATH = 'storage/ricette'
Set-Location ProgettoSpring\LBP-App-Vet
.\mvnw.cmd spring-boot:run
```

The backend listens on <http://localhost:9020>. Flyway still runs automatically against the configured local database.

### Frontend

In another terminal:

```powershell
Set-Location React\vetmanager-portal
npm ci
npm run dev
```

Vite serves the development client and proxies `/api` to `http://localhost:9020`.

## Quality checks

Run backend tests:

```powershell
Set-Location ProgettoSpring\LBP-App-Vet
.\mvnw.cmd test
```

The Spring integration test uses Testcontainers when Docker is available and verifies Flyway, Hibernate schema validation, seed data, and migration idempotency.

Run frontend checks:

```powershell
Set-Location React\vetmanager-portal
npm ci
npm run lint
npm run build
```

## Troubleshooting

### A service is unhealthy

Run `docker compose ps` and inspect the affected service with `docker compose logs --tail 200 <service>`. The backend will not start until PostgreSQL is healthy, and the frontend waits for the backend readiness check.

### Port 8080 is already in use

Change `FRONTEND_PORT` in `.env`, then run `docker compose up -d` again.

### Backend reports a database connection error

Check the database health and verify that `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` are present in `.env`. Inside Compose, the JDBC hostname must remain `database`, not `localhost`.

### Flyway checksum validation fails

An already-applied migration was probably changed. Restore that migration to its committed contents and create a new versioned migration for the correction. Do not delete `flyway_schema_history` to bypass validation.

### Hibernate schema validation fails

The entity model and migration scripts disagree. Add a new migration or correct an unapplied migration; do not switch Hibernate to automatic schema creation.

### Email sending fails with the example environment

This is expected because `smtp.example.invalid` is deliberately non-functional. Add valid SMTP values only to the ignored `.env`. Keep TLS and certificate verification enabled.

### Docker commands cannot reach the engine

Start Docker Desktop or the Docker daemon and confirm that the current user can access it with `docker version`.

## Repository layout

```text
LearningByProject/
|-- compose.yml
|-- .env.example
|-- ProgettoSpring/LBP-App-Vet/    # Spring Boot backend and Flyway migrations
|-- React/vetmanager-portal/       # React frontend and Nginx configuration
|-- DatiExcel/                     # Import and reference data
|-- Documentazione/                # Project documentation
`-- Mockup/                        # Earlier UI mockups
```

This remains a development and learning project. Review authentication, authorization, data protection, mail delivery, backups, and operational monitoring before considering production use.
