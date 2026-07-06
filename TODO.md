# Vet Portal TODO

This roadmap is based on the current project state: documentation and mockups exist, the Spring Boot backend has entities/controllers/services started, but the frontend, database migration flow, tests, and deployment setup still need structure.

## Phase 0 - Immediate Cleanup And Safety

- [ ] Move database, mail, and JWT secrets out of `application.yml`.
- [ ] Create an `application-example.yml` with placeholder values only.
- [ ] Rotate any credentials that were committed or shared in plain text.
- [ ] Decide the official naming convention for Java packages and API paths.
- [ ] Align the README project structure with the real repository structure.
- [ ] Fix visible encoding issues in Markdown files so accented Italian text renders correctly.
- [ ] Convert the active database schema into repeatable migration files.

## Phase 1 - Backend Foundation

- [ ] Confirm the Spring Boot version to use and keep it consistent with the README.
- [ ] Add DTOs for request/response payloads instead of exposing entities directly.
- [ ] Add validation annotations to DTOs and controller inputs.
- [ ] Add a global exception handler for validation errors, missing records, and authorization failures.
- [ ] Standardize CRUD endpoint names across controllers.
- [ ] Add soft-delete behavior consistently for entities that have `isDeleted`.
- [ ] Add JavaDoc to classes, interfaces, and public methods according to `CodeRequirements.md`.
- [ ] Verify entity relationships against `Documentazione/DB/Tabelle_EasyRead.md`.
- [ ] Add seed data for roles, users, visit categories, visit types, and vaccine types.

## Phase 2 - Authentication And Roles

- [ ] Complete login flow with JWT generation and validation.
- [ ] Add password hashing for users.
- [ ] Define roles clearly: `ADMIN`, `VET`, `RECEPTION`, `CLIENT`.
- [ ] Protect endpoints with role-based authorization.
- [ ] Add endpoints for current user profile and logout/client token cleanup strategy.
- [ ] Add tests for successful login, failed login, and role-restricted access.

## Phase 3 - Core Appointment Workflow

- [ ] Implement available visit slots from weekly schedules.
- [ ] Add appointment creation for clients and receptionists.
- [ ] Add appointment modification and cancellation.
- [ ] Add appointment status transitions such as booked, confirmed, completed, cancelled.
- [ ] Add filters for upcoming appointments by role.
- [ ] Add doctor dashboard endpoint for today's appointments.
- [ ] Add receptionist dashboard endpoint for incoming and pending appointments.
- [ ] Add handling for first-visit requests and generic `Other` animal type.

## Phase 4 - Medical Records And Files

- [ ] Define the medical record workflow for each visit.
- [ ] Add visit notes, private notes, prescriptions, and attachments.
- [ ] Restrict medical record editing to doctor/admin roles.
- [ ] Allow clients to view their own medical records in read-only mode.
- [ ] Implement PDF-only upload/download for clinical files.
- [ ] Add file metadata persistence through `FileReferences`.
- [ ] Add audit events for file upload, download, and medical record updates.

## Phase 5 - Payments And Invoices

- [ ] Define payment statuses and payment methods.
- [ ] Link visits, payments, users, and invoice file references consistently.
- [ ] Add endpoints to mark invoices as paid or unpaid.
- [ ] Add invoice download endpoint.
- [ ] Add receptionist payment recording workflow.
- [ ] Add basic payment report export.

## Phase 6 - Notifications And Email

- [ ] Build a mail service around the existing HTML templates.
- [ ] Send registration confirmation emails.
- [ ] Send appointment confirmation emails.
- [ ] Send appointment cancellation emails.
- [ ] Send delay notifications from receptionist workflow.
- [ ] Send vaccination or appointment reminder emails.
- [ ] Add configuration to disable real email sending in local development/tests.

## Phase 7 - Frontend Application

- [ ] Scaffold the React app.
- [ ] Add routing and role-based protected pages.
- [ ] Build login and authenticated layout.
- [ ] Build client dashboard: pets, appointments, records, invoices.
- [ ] Build receptionist dashboard: calendar, booking management, payments, delays.
- [ ] Build doctor dashboard: daily visits, patient chart, notes, prescriptions.
- [ ] Connect frontend services to backend APIs.
- [ ] Reuse the existing mockup as visual guidance where practical.
- [ ] Add loading, empty, error, and permission-denied states.

## Phase 8 - Tests And Quality

- [ ] Add backend unit tests for services.
- [ ] Add backend integration tests for controllers.
- [ ] Add repository tests for important queries.
- [ ] Add authentication and authorization test coverage.
- [ ] Add frontend component tests for major pages.
- [ ] Add end-to-end tests for login, booking, visit update, and payment recording.
- [ ] Add a local test database profile.
- [ ] Add formatting/linting checks.

## Phase 9 - Local Development And Deployment

- [ ] Add `docker-compose.yml` for local PostgreSQL and backend.
- [ ] Document local startup steps.
- [ ] Add environment-specific Spring profiles: local, dev, prod.
- [ ] Add GitHub Actions for backend tests.
- [ ] Add GitHub Actions for frontend tests after React is added.
- [ ] Decide hosting target for backend, database, file storage, and frontend.
- [ ] Add backup strategy for database and uploaded files.
- [ ] Add basic monitoring/logging strategy.

## Suggested Next Sprint

- [ ] Secure configuration and rotate credentials.
- [ ] Add DTOs and validation for `Animale` and `Azienda`, because TODO comments already identify them.
- [ ] Standardize controller endpoint naming for one module, then apply the pattern elsewhere.
- [ ] Implement role-based protection for existing APIs.
- [ ] Create the first real end-to-end flow: login -> list animals -> book appointment.
