# VetManager frontend

This directory contains the React/Vite client for VetManager. It provides public pages and role-specific areas for clients, veterinarians, receptionists, and administrators.

## Runtime architecture

The production container uses a two-stage build:

1. Node installs locked dependencies and creates the Vite production bundle.
2. Nginx serves the static files, handles single-page application fallbacks, and proxies `/api` requests to `backend:9020`.

The client uses relative API paths. Do not configure browser code with a Compose hostname: `backend` is resolvable only between containers. In Docker, Nginx performs the routing. During local development, Vite proxies the same `/api` paths to `http://localhost:9020`.

## Prerequisites

- Node.js 24 and npm for local development
- Docker and Docker Compose when running the complete stack

## Local development

Start the Spring Boot backend on port 9020, then run:

```powershell
npm ci
npm run dev
```

Use the URL printed by Vite. Requests beginning with `/api` are forwarded according to `vite.config.js`.

## Available scripts

```powershell
npm run dev
npm run lint
npm run build
npm run preview
```

- `dev` starts the Vite development server.
- `lint` runs ESLint over the frontend source.
- `build` creates the optimized static bundle in `dist`.
- `preview` serves the generated bundle for a local preview; it is not the production server.

## Container build

Build this image by itself from this directory:

```powershell
docker build -t vetmanager-frontend .
```

For normal use, start the complete environment from the repository root:

```powershell
docker compose up --build -d
```

The root Compose environment publishes Nginx on port 8080 by default. The Spring Boot and PostgreSQL ports remain internal.

## Routing and health

Nginx configuration is stored in `nginx.conf`:

- `/` serves React and falls back to `index.html` for client-side routes.
- `/api/*` is proxied to Spring Boot.
- `/api/health` is proxied to the Spring Boot readiness endpoint.
- `/health` checks only the Nginx frontend container.

With the default root configuration:

```powershell
Invoke-WebRequest http://localhost:8080/health
Invoke-RestMethod http://localhost:8080/api/health
```

## Configuration and secrets

Frontend configuration shipped by Vite is visible to every browser user. Never place database passwords, email credentials, JWT signing keys, or other secrets in frontend source code or `VITE_*` variables.

Authentication tokens received after login are currently stored in browser local storage and added as bearer tokens by `src/services/apiClient.js`. API calls should continue using relative `/api` paths so the same code works behind Nginx and the local Vite proxy.

## Important files

```text
Dockerfile             Multi-stage production image
nginx.conf             Static hosting, SPA fallback, and API proxy
vite.config.js         Local development API proxy
src/services/          Shared API client and domain API modules
src/routes/            Public and protected routes
src/context/           Shared application and authentication state
src/pages/             Role-oriented application pages
```

The frontend currently has lint and production-build checks but no automated browser or component test suite.
