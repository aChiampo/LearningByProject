# Vet Portal TODO

Current state date: 2026-07-07.

This roadmap reflects the current repository state. The backend has a Spring Boot application with entities, repositories, services, controllers, JWT authentication, roles, mail templates, PDF/template services, and one smoke test. The frontend has a Vite React application with role-based mock screens, but it is not yet integrated with the backend APIs.

## Current Status Snapshot

- [x] Spring Boot backend project exists.
- [x] React/Vite frontend project exists in `React/vetmanager-portal`.
- [x] Main domain entities exist: users, roles, animals, species, breeds, visits, vaccines, payments, schedules, file references, categories, companies.
- [x] Repository/service/controller layers exist for most main entities.
- [x] JWT login endpoint exists.
- [x] JWT filter is wired into Spring Security.
- [x] BCrypt password hashing is used when users are created or updated through `UtenteService`.
- [x] `Ruolo` entity, repository, service, and controller exist.
- [x] Mail templates exist under Spring resources.
- [x] Basic email sender service exists.
- [x] PDF/template services exist for invoices and prescriptions.
- [ ] Backend tests pass locally.
- [ ] Frontend build is verified locally.
- [ ] Frontend calls backend APIs.
- [ ] API permissions are complete by role.
- [ ] Secrets are removed from committed configuration.

## Phase 0 - Immediate Cleanup And Safety

- [ ] Move database, mail, and JWT secrets out of `application.yml`.
- [ ] Create `application-example.yml` with placeholder values only.
- [ ] Rotate credentials that were committed or shared in plain text.
- [ ] Add `application-local.yml` and `application-test.yml`.
- [ ] Make backend tests use a local/test database instead of the external PostgreSQL connection.
- [ ] Decide the official naming convention for Java packages, table names, and API paths.
- [ ] Align the README project structure with the real repository structure.
- [ ] Fix visible encoding issues in Markdown, Java comments, and React text.
- [ ] Convert the active database schema into repeatable migration files.

## Phase 1 - Backend Foundation

- [x] Confirm Java 21 is configured.
- [x] Create core entities, repositories, services, and controllers.
- [x] Add role persistence through `Ruolo`.
- [ ] Confirm whether Spring Boot 4.1.0 is intentional or downgrade/align with the README target.
- [ ] Add DTOs for request/response payloads instead of exposing entities directly.
- [ ] Add validation annotations to DTOs and controller inputs.
- [ ] Add a global exception handler for validation errors, missing records, and authorization failures.
- [ ] Standardize CRUD endpoint names across controllers.
- [ ] Add soft-delete behavior consistently for entities that have `isDeleted`.
- [ ] Complete JavaDoc coverage according to `CodeRequirements.md`.
- [ ] Verify all entity relationships against `Documentazione/DB/Tabelle_EasyRead.md`.
- [ ] Add seed data for roles, users, visit categories, visit types, species, breeds, and vaccine types.

## Phase 2 - Authentication And Roles

- [x] Add login endpoint.
- [x] Generate JWT after successful login.
- [x] Validate JWT on protected requests.
- [x] Hash passwords when users are created or updated through `UtenteService`.
- [x] Include user id, email, and role claims in JWT.
- [x] Add role entity/table support.
- [ ] Define the final role names and keep backend/frontend names consistent.
- [ ] Add login request validation with `@Valid`, `@Email`, and `@NotBlank`.
- [ ] Extend login response with user id, email, name, and role.
- [ ] Add `/api/auth/me` or equivalent current-user endpoint.
- [ ] Add clear role-based authorization rules per endpoint.
- [ ] Add tests for successful login, failed login, expired/invalid token, and role-restricted access.
- [ ] Decide logout strategy for the frontend.

## Phase 3 - Core Appointment Workflow

- [x] Visit, visit type, category, animal, user, and weekly schedule models exist.
- [x] Visit-related controllers/services/repositories exist.
- [ ] Decide whether `Visita` is also the booking/appointment entity or whether a separate appointment entity is required.
- [ ] Implement available visit slots from weekly schedules.
- [ ] Add appointment creation for clients and receptionists.
- [ ] Add appointment modification and cancellation.
- [ ] Add appointment status transitions such as booked, confirmed, completed, and cancelled.
- [ ] Add filters for upcoming appointments by role.
- [ ] Add doctor dashboard endpoint for today's appointments.
- [ ] Add receptionist dashboard endpoint for incoming and pending appointments.
- [ ] Add handling for first-visit requests and generic `Other` animal type.

## Phase 4 - Medical Records, Files, And PDFs

- [x] `FileReferences` entity/service/controller exists.
- [x] PDF file service exists.
- [x] Invoice and prescription templates exist under Spring resources.
- [ ] Define the medical record workflow for each visit.
- [ ] Add visit notes, private notes, prescriptions, and attachments as a complete workflow.
- [ ] Restrict medical record editing to doctor/admin roles.
- [ ] Allow clients to view their own medical records in read-only mode.
- [ ] Implement PDF-only upload/download rules for clinical files.
- [ ] Add file metadata persistence and file lifecycle handling.
- [ ] Add audit events for file upload, download, and medical record updates.
- [ ] Add tests for PDF generation/file reference behavior.

## Phase 5 - Payments And Invoices

- [x] Payment entity/repository/service/controller exists.
- [x] Invoice templates exist for private clients and companies.
- [ ] Define final payment statuses and payment methods.
- [ ] Link visits, payments, users, and invoice file references consistently.
- [ ] Add endpoints to mark invoices as paid or unpaid.
- [ ] Add invoice generation and download endpoint.
- [ ] Add receptionist payment recording workflow.
- [ ] Add basic payment report export.
- [ ] Add tests for payment state changes and invoice generation.

## Phase 6 - Notifications And Email

- [x] Email sender service exists.
- [x] Mail templates exist for registration, appointment confirmation, appointment cancellation, delay notice, new invoice, and vaccination reminder.
- [ ] Build typed service methods around each mail template.
- [ ] Send registration confirmation emails from the user creation flow.
- [ ] Send appointment confirmation emails from the booking flow.
- [ ] Send appointment cancellation emails from the cancellation flow.
- [ ] Send delay notifications from receptionist workflow.
- [ ] Send vaccination or appointment reminder emails.
- [ ] Add configuration to disable real email sending in local development/tests.
- [ ] Add tests that verify template rendering without sending real emails.

## Phase 7 - Frontend Application

- [x] Scaffold React/Vite app.
- [x] Add public homepage.
- [x] Add role-based mock navigation.
- [x] Add mock dashboards for client, doctor, receptionist, and admin/super-admin.
- [x] Add local React context for role/screen state.
- [ ] Replace demo role switch login with real email/password login.
- [ ] Add API client layer.
- [ ] Store and send JWT in authenticated requests.
- [ ] Add route handling instead of only screen-state switching.
- [ ] Align frontend role names with backend role names.
- [ ] Connect animals, visits, users, payments, and dashboard screens to backend APIs.
- [ ] Add loading, empty, error, and permission-denied states for API data.
- [ ] Fix encoding issues in rendered text.
- [ ] Verify `npm run build`; current sandbox run was blocked by `spawn EPERM` from Vite/Rolldown.

## Phase 8 - Tests And Quality

- [ ] Fix backend test profile so `mvnw test` does not require the external PostgreSQL database.
- [ ] Add backend unit tests for services.
- [ ] Add backend integration tests for controllers.
- [ ] Add repository tests for important queries.
- [ ] Add authentication and authorization test coverage.
- [ ] Add PDF/template rendering tests.
- [ ] Add frontend component tests for major screens.
- [ ] Add end-to-end tests for login, booking, visit update, and payment recording.
- [ ] Add frontend lint/build verification to the normal workflow.
- [ ] Add formatting checks.

## Phase 9 - Local Development And Deployment

- [ ] Add `docker-compose.yml` for local PostgreSQL and backend.
- [ ] Document local startup steps for backend and frontend.
- [ ] Add environment-specific Spring profiles: local, test, dev, prod.
- [ ] Add GitHub Actions for backend tests.
- [ ] Add GitHub Actions for frontend tests/build after React build is stable.
- [ ] Decide hosting target for backend, database, file storage, and frontend.
- [ ] Add backup strategy for database and uploaded files.
- [ ] Add basic monitoring/logging strategy.

## Suggested Next Sprint

- [ ] Remove committed secrets, rotate credentials, and add example/local/test config files.
- [ ] Fix the backend test profile so `mvnw test` passes without the external database.
- [ ] Add DTOs and validation for login, users, animals, visits, and payments.
- [ ] Add `/api/auth/me` and richer login response data.
- [ ] Define final role names and apply role-based permissions to existing APIs.
- [ ] Replace the frontend demo login with the real backend login flow.
- [ ] Connect one complete vertical flow: login -> list animals -> create visit/appointment -> view appointment.
