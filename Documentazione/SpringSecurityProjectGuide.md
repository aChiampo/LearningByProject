# Spring Security in This Project

**Index**

- [Main Files](#main-files)
- [Simple Explanation](#simple-explanation)
- [Security Configuration](#security-configuration)
- [Login Flow Example](#login-flow-example)
- [Protected Request Flow Example](#protected-request-flow-example)
- [Roles and Authorities](#roles-and-authorities)
- [How to Use Security in Controllers](#how-to-use-security-in-controllers)
- [How to Use Security in Services](#how-to-use-security-in-services)
- [Password Handling During Development](#password-handling-during-development)
- [Common Development Checklist](#common-development-checklist)
- [Current Project Notes](#current-project-notes)
- [Example End-to-End Flow](#example-end-to-end-flow)

This document explains how Spring Security works in the `LBP-App-Vet` project and how to use it while developing controllers and services.

The project uses Spring Security with JWT Bearer tokens. This means the backend does not keep a server-side login session. Each protected request must contain a valid token in the HTTP `Authorization` header.

```http
Authorization: Bearer <jwt-token>
```

## Main Files

The security logic is mainly in these files:

- `ConfigurazioneSicurezza.java`
- `FiltroAutenticazioneJwt.java`
- `ServizioJwt.java`
- `AutenticazioneController.java`
- `UtenteService.java`

## Simple Explanation

Spring Security sits before the controllers. Every HTTP request enters the Spring Security filter chain first.

In this project:

1. Some public endpoints are allowed without login.
2. All other endpoints require authentication.
3. `DELETE /api/**` endpoints require the `ADMIN` role.
4. A custom JWT filter reads the token from the request.
5. If the token is valid, Spring Security stores the authenticated user in the `SecurityContext`.
6. The controller can then use the authenticated user and Spring can check role annotations like `@PreAuthorize`.

## Security Configuration

`ConfigurazioneSicurezza` defines the main rules:

```java
@Bean
public SecurityFilterChain catenaFiltriSicurezza(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(configurazioneCors()))
            .sessionManagement(sessione -> sessione.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(autorizzazioni -> autorizzazioni
                    .requestMatchers("/api/auth/**", "/api/autenticazione/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(filtroAutenticazioneJwt, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

Important points:

- `csrf(...disable...)`: CSRF protection is disabled because this is a stateless API that uses Bearer tokens.
- `cors(...)`: allows the React frontend to call the backend from `localhost:5173` and `localhost:3000`.
- `STATELESS`: Spring will not create an HTTP session.
- `/api/auth/**` and `/api/autenticazione/**`: public login endpoints.
- `DELETE /api/**`: only users with role `ADMIN`.
- `anyRequest().authenticated()`: every other endpoint needs a valid login token.
- `addFilterBefore(...)`: the JWT filter runs before the standard username/password filter.

The class also has `@EnableMethodSecurity`, which enables annotations such as:

```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN','VET','REC')")
```

## Login Flow Example

Imagine a user logs in with email and password.

### 1. Client Calls Login

The frontend sends:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "mario.rossi@example.com",
  "password": "secret-password"
}
```

This endpoint is public because `ConfigurazioneSicurezza` has:

```java
.requestMatchers("/api/auth/**", "/api/autenticazione/**").permitAll()
```

### 2. Controller Checks Credentials

`AutenticazioneController` loads the user by email:

```java
Utente utente = utenteRepo.findByEmail(richiesta.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide."));
```

Then it compares the plain password sent by the client with the hashed password stored in the database:

```java
if (!codificatorePassword.matches(richiesta.password(), utente.getPasswordHash())) {
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
}
```

The password is not decoded. BCrypt hashes are one-way. `PasswordEncoder.matches(...)` hashes the received password and checks whether it matches the saved hash.

### 3. Backend Creates the JWT

If the credentials are valid, `ServizioJwt` creates a token:

```java
String token = servizioJwt.creaToken(utente);
```

The token contains:

- `subject`: the user id, for example `"5"`
- `role`: the user role, for example `"ADMIN"`
- `issuedAt`: when the token was created
- `expiration`: when the token expires
- a signature created with `app.security.jwt.secret`

The secret is read from:

```properties
app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration-seconds=3600
```

So during development, the application needs a `JWT_SECRET` environment variable.

### 4. Backend Returns the Token

The response looks like this:

```json
{
  "token": "<jwt-token>",
  "tipoToken": "Bearer",
  "scadenzaSecondi": 3600
}
```

The frontend must store this token and send it in the `Authorization` header for later requests.

## Protected Request Flow Example

Now imagine the frontend calls:

```http
GET /api/animali/leggi/12
Authorization: Bearer <jwt-token>
```

The flow is:

1. The request reaches the Spring Security filter chain.
2. `FiltroAutenticazioneJwt` reads the `Authorization` header.
3. If the header does not start with `Bearer `, the request continues without authentication and Spring Security will reject it later.
4. If the token exists, the filter calls `servizioJwt.leggiEValida(token)`.
5. `ServizioJwt` verifies the JWT signature and expiration.
6. If the token is valid, the filter reads:
   - the user id from `claims.getSubject()`
   - the role from `claims.get("role", String.class)`
7. The filter creates a Spring authentication object:

```java
UsernamePasswordAuthenticationToken autenticazione =
        new UsernamePasswordAuthenticationToken(
                idUtente,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + ruolo))
        );
```

8. The authentication is saved in:

```java
SecurityContextHolder.getContext().setAuthentication(autenticazione);
```

9. The request reaches the controller only if the security rules allow it.

In controllers, `Authentication.getName()` returns the user id because the JWT filter stores `idUtente` as the principal.

Example from `AnimaleController`:

```java
public ResponseEntity<Animale> leggi(@PathVariable Integer id, Authentication autenticazione) {
    int userId = Integer.parseInt(autenticazione.getName());
    ...
}
```

## Roles and Authorities

Spring Security distinguishes between roles and authorities.

This project stores the JWT role claim as a simple value:

```text
ADMIN
VET
REC
CLIENTE
```

Then the JWT filter converts it to a Spring authority by adding `ROLE_`:

```java
new SimpleGrantedAuthority("ROLE_" + ruolo)
```

So:

- JWT claim `"role": "ADMIN"` becomes Spring authority `ROLE_ADMIN`
- `hasRole("ADMIN")` checks for `ROLE_ADMIN`
- `hasAnyRole("ADMIN", "VET")` checks for `ROLE_ADMIN` or `ROLE_VET`

Do not write this:

```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
```

Use this instead:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Or, if you want to check the full authority name:

```java
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
```

## How to Use Security in Controllers

### 1. Decide Whether the Endpoint Is Public

Most endpoints are protected by default because of:

```java
.anyRequest().authenticated()
```

If you create a new endpoint and it should be public, add it to `ConfigurazioneSicurezza`.

Example:

```java
.requestMatchers("/api/auth/**", "/api/autenticazione/**", "/api/public/**").permitAll()
```

Only make endpoints public when they really do not need a logged-in user.

### 2. Add Role Rules with `@PreAuthorize`

Use `@PreAuthorize` on controller methods when different roles should have different permissions.

Example:

```java
@PreAuthorize("hasAnyRole('ADMIN','VET','REC')")
@GetMapping("/leggiTutti")
public ResponseEntity<List<Animale>> leggiTutti() {
    return ResponseEntity.ok(animaleService.ottieniTutti());
}
```

Example for admin-only logic:

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/elimina/{id}")
public ResponseEntity<Void> elimina(@PathVariable Integer id) {
    animaleService.eliminaAnimale(id);
    return ResponseEntity.noContent().build();
}
```

The project already has a global rule that protects all `DELETE /api/**` endpoints with `ADMIN`, but adding `@PreAuthorize("hasRole('ADMIN')")` can still make the controller method easier to understand.

### 3. Read the Current User Id

When a controller needs to know who is calling the endpoint, add an `Authentication` parameter:

```java
@GetMapping("/profilo")
public ResponseEntity<Utente> profilo(Authentication authentication) {
    Integer userId = Integer.valueOf(authentication.getName());
    return ResponseEntity.ok(utenteService.ottieniPerId(userId));
}
```

This works because `FiltroAutenticazioneJwt` uses the JWT subject as the authentication name.

### 4. Check Ownership When Needed

Role checks answer this question:

```text
Can this type of user call this endpoint?
```

Ownership checks answer this question:

```text
Is this specific resource owned by this specific user?
```

For example, a `CLIENTE` may be allowed to read an animal, but only if that animal belongs to them.

Example pattern:

```java
@PreAuthorize("hasAnyRole('ADMIN','VET','REC','CLIENTE')")
@GetMapping("/leggi/{id}")
public ResponseEntity<Animale> leggi(@PathVariable Integer id, Authentication authentication) {
    Integer userId = Integer.valueOf(authentication.getName());
    Animale animale = animaleService.ottieniPerId(id);

    boolean isOwner = animale.getUtente() != null && animale.getUtente().getId().equals(userId);
    boolean isStaff = authentication.getAuthorities().stream()
            .anyMatch(a -> List.of("ROLE_ADMIN", "ROLE_VET", "ROLE_REC").contains(a.getAuthority()));

    if (!isOwner && !isStaff) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(animale);
}
```

In real development, prefer moving complex ownership checks into the service layer or into a dedicated authorization helper, especially if the same rule is used by multiple controllers.

## How to Use Security in Services

Services should contain business rules. Controllers should remain thin.

Good responsibilities:

- Controller: receives the HTTP request, reads `Authentication`, calls the service.
- Service: validates business rules, checks ownership if needed, reads and writes the database.
- Repository: only accesses the database.

Example:

```java
@Service
@RequiredArgsConstructor
public class AnimaleService {

    private final AnimaleRepository animaleRepository;

    @Transactional(readOnly = true)
    public Animale ottieniPerIdSeAutorizzato(Integer animaleId, Integer userId, boolean isStaff) {
        Animale animale = ottieniPerId(animaleId);

        boolean isOwner = animale.getUtente() != null && animale.getUtente().getId().equals(userId);

        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso non autorizzato.");
        }

        return animale;
    }
}
```

Controller:

```java
@PreAuthorize("hasAnyRole('ADMIN','VET','REC','CLIENTE')")
@GetMapping("/leggi/{id}")
public ResponseEntity<Animale> leggi(@PathVariable Integer id, Authentication authentication) {
    Integer userId = Integer.valueOf(authentication.getName());
    boolean isStaff = authentication.getAuthorities().stream()
            .anyMatch(a -> List.of("ROLE_ADMIN", "ROLE_VET", "ROLE_REC").contains(a.getAuthority()));

    return ResponseEntity.ok(animaleService.ottieniPerIdSeAutorizzato(id, userId, isStaff));
}
```

This keeps the security decision close to the business data.

## Password Handling During Development

The project uses:

```java
@Bean
public PasswordEncoder codificatorePassword() {
    return new BCryptPasswordEncoder();
}
```

`UtenteService` uses it when creating or updating users:

```java
utente.setPasswordHash(passwordEncoder.encode(utente.getPasswordHash()));
```

Development rules:

- Never save a plain password in the database.
- Never return `passwordHash` in an API response.
- Use `PasswordEncoder.encode(...)` before saving a new password.
- Use `PasswordEncoder.matches(...)` only when checking login credentials.

The `Utente.passwordHash` field has `@JsonIgnore`, so it should not be serialized in JSON responses.

## Common Development Checklist

When creating a new controller endpoint:

1. Choose the URL under `/api/...`.
2. Decide whether it is public or protected.
3. If public, update `ConfigurazioneSicurezza`.
4. If protected, do nothing for basic authentication because `.anyRequest().authenticated()` already protects it.
5. Add `@PreAuthorize` if only some roles can call it.
6. Add an `Authentication` parameter if you need the current user id.
7. Check resource ownership when the endpoint reads or modifies user-specific data.
8. Keep complex authorization logic in the service layer.

When creating or changing a service:

1. Accept the current user id from the controller when the business rule depends on the caller.
2. Check that the user can access or modify the requested resource.
3. Throw `ResponseStatusException(HttpStatus.FORBIDDEN, "...")` when the user is authenticated but not allowed.
4. Throw `ResponseStatusException(HttpStatus.NOT_FOUND, "...")` when the resource does not exist.
5. Use `@Transactional` for database write operations.
6. Use `@Transactional(readOnly = true)` for read-only operations.

## Current Project Notes

These are important details to remember while developing this specific project:

- `Authentication.getName()` is the user id, not the email.
- The JWT role claim should be a simple role name such as `ADMIN`, not `ROLE_ADMIN`.
- The JWT filter adds the `ROLE_` prefix for Spring Security.
- `DELETE /api/**` is globally limited to `ADMIN`.
- Login is available from both `/api/auth/login` and `/api/autenticazione/login`.
- The project expects `JWT_SECRET` to exist in the environment.
- `@CrossOrigin(origins = "*")` appears on some controllers, but CORS is already centrally configured in `ConfigurazioneSicurezza`.
- If the `Ruolo` model is changed, make sure `ServizioJwt.creaToken(...)` still writes the role claim in the exact format expected by the JWT filter.

## Example End-to-End Flow

Example: a receptionist reads all animals.

1. The receptionist logs in with:

```http
POST /api/auth/login
```

2. The backend validates the email and password.
3. The backend returns a JWT with:

```json
{
  "sub": "7",
  "role": "REC"
}
```

4. The frontend calls:

```http
GET /api/animali/leggiTutti
Authorization: Bearer <jwt-token>
```

5. `FiltroAutenticazioneJwt` validates the token.
6. The filter creates an authenticated user with:

```text
name = "7"
authority = "ROLE_REC"
```

7. Spring checks the controller annotation:

```java
@PreAuthorize("hasAnyRole('ADMIN','VET','REC')")
```

8. `ROLE_REC` is allowed, so the controller method runs.
9. The controller calls `animaleService.ottieniTutti()`.
10. The API returns the list of animals.

If the same user tries:

```http
DELETE /api/animali/elimina/12
Authorization: Bearer <jwt-token>
```

Spring Security blocks the request because `DELETE /api/**` requires `ADMIN`.
