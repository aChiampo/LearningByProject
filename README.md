# VetManager Portal

VetManager Portal e un progetto didattico per una web application dedicata a una
clinica veterinaria. Il progetto e composto da un backend Spring Boot e da un
frontend React/Vite per medico veterinario, receptionist, clienti e area admin.

Il progetto e attualmente in sviluppo attivo. Contiene basi backend e frontend
funzionanti, ma non e ancora pronto per un utilizzo in produzione.

## Contesto Del Progetto

Il Dott. Camillo Zampetti gestisce uno studio veterinario a Bergamo Alta con
circa 280 pazienti attivi, tra cani, gatti e piccoli animali.

L'obiettivo e ridurre il lavoro manuale ripetitivo, come telefonate per le
prenotazioni, richieste via WhatsApp, ricette smarrite, informazioni cliniche
incomplete e libretti vaccinali cartacei.

L'applicazione deve permettere agli utenti autenticati di accedere a flussi
diversi in base al ruolo:

- Cliente: gestione animali, prenotazione appuntamenti, consultazione di
  appuntamenti, cartelle cliniche, pagamenti e fatture.
- Receptionist: gestione appuntamenti, clienti, notifiche di ritardo e pagamenti.
- Medico/Veterinario: agenda giornaliera, cartelle animali, visite, vaccinazioni,
  report visita e dati di configurazione.
- Admin/Super admin: gestione amministrativa e aree di sistema.

## Struttura Reale Del Repository

```text
LearningByProject/
|-- ProgettoSpring/
|   `-- LBP-App-Vet/
|       |-- pom.xml
|       |-- mvnw
|       |-- mvnw.cmd
|       `-- src/
|           |-- main/
|           |   |-- java/com/WW/
|           |   |   |-- autenticazione/
|           |   |   |-- controllers/
|           |   |   |-- dto/
|           |   |   |-- entities/
|           |   |   |-- enums/
|           |   |   |-- exception/
|           |   |   |-- fileManager/
|           |   |   |-- mailManager/
|           |   |   |-- repositories/
|           |   |   |-- sicurezza/
|           |   |   `-- services/
|           |   `-- resources/
|           |       |-- application.yml
|           |       `-- templates/
|           |           |-- fatture/
|           |           |-- mail/
|           |           `-- ricette/
|           `-- test/java/com/WW/
|-- React/
|   `-- vetmanager-portal/
|       |-- package.json
|       |-- vite.config.js
|       |-- public/
|       `-- src/
|           |-- components/
|           |-- context/
|           |-- data/
|           |-- layouts/
|           |-- pages/
|           |-- routes/
|           `-- services/
|-- DatiExcel/
|-- Documentazione/
|-- Mockup/
|-- StudyMaterial/
|-- CodeRequirements.md
|-- TODO.md
`-- README.md
```

Al momento nel repository reale non sono presenti una directory
`.github/workflows`, un file `docker-compose.yml` o una cartella `database/`
dedicata alle migrazioni.

## Stack Tecnico

| Livello | Tecnologia attuale |
| --- | --- |
| Frontend | React 19, Vite 8, React Router 7 |
| Backend | Java 21, Spring Boot 4.1.0 |
| Sicurezza | Spring Security, JWT, BCrypt |
| Database | PostgreSQL |
| Persistenza | Spring Data JPA |
| Template | Template HTML Thymeleaf |
| Generazione PDF | openhtmltopdf |
| Mail | Spring Mail |
| Package manager | npm, Maven Wrapper |

## Stato Attuale Del Backend

Il backend si trova in `ProgettoSpring/LBP-App-Vet`.

Aree implementate o parzialmente implementate:

- Applicazione Spring Boot nel package `com.WW`.
- Layer entity, repository, service e controller per i principali modelli di
  dominio.
- Endpoint di login e registrazione JWT sotto `/api/auth` e
  `/api/autenticazione`.
- Filtro JWT collegato a Spring Security.
- Supporto ai ruoli tramite entita `Ruolo`.
- Hashing BCrypt quando gli utenti vengono creati o aggiornati tramite
  `UtenteService`.
- Gestore globale delle eccezioni per errori di validazione, response status e
  illegal argument.
- Flussi appuntamento/visita basati su `Visita`.
- Ricerca slot disponibili, prenotazione, riprogrammazione, cancellazione e
  chiusura visita.
- Animali, utenti, visite, tipi visita, categorie, specie, razze, vaccinazioni,
  pagamenti, orari, aziende, riferimenti file e richieste di valutazione animale.
- Template mail per registrazione, conferma appuntamento, cancellazione
  appuntamento, avviso ritardo, notifica fattura e promemoria vaccinale.
- Servizi PDF e template HTML per fatture e ricette.

Gap importanti del backend:

- I segreti sono ancora presenti in `application.yml`; devono essere spostati in
  variabili d'ambiente e le credenziali esposte devono essere ruotate prima di
  usare ambienti condivisi.
- Non esiste ancora una configurazione separata per profili local/test/prod.
- Diversi controller accettano o restituiscono direttamente entita JPA invece di
  DTO stabili per request/response.
- I path API e le convenzioni di naming alternano stile italiano e inglese.
- L'autorizzazione esiste, ma i permessi per ruolo devono ancora essere
  verificati endpoint per endpoint.
- Lo schema database non e ancora gestito tramite file di migrazione versionati.
- I test backend sono minimi: e presente solo uno smoke test del contesto Spring.

## Stato Attuale Del Frontend

Il frontend si trova in `React/vetmanager-portal`.

Aree implementate o parzialmente implementate:

- App React/Vite con pagine basate su routing.
- Home page pubblica, pagina login, pagina registrazione e pagina primo
  appuntamento.
- Rotte protette e rotte basate sul ruolo.
- Context condiviso per stato di autenticazione e sessione.
- Client API che salva la sessione JWT nel local storage del browser e invia
  header `Authorization: Bearer ...`.
- Integrazione backend avviata per autenticazione, utente corrente, animali,
  appuntamenti/visite, tipi visita, vaccinazioni, utenti e pagamenti.
- Aree di ruolo per cliente, medico, receptionist e super admin.
- Flusso di prenotazione cliente con ricerca degli slot disponibili.
- Registro animali del medico e schermate cartella clinica/report visita.
- Schermate receptionist per appuntamenti e pagamenti.

Gap importanti del frontend:

- Alcune sezioni frontend sono ancora mock o placeholder.
- Non sono ancora presenti test frontend.
- `npm run lint` richiede ancora interventi prima di poter essere considerato una
  quality gate pulita.
- Stati di errore, caricamento e vuoto sono presenti in diversi punti, ma non
  sono ancora completi in tutte le schermate.
- Il frontend dipende dalla disponibilita del backend tramite proxy Vite `/api`.

## Sviluppo Locale

### Backend

Dalla cartella del backend:

```powershell
cd ProgettoSpring\LBP-App-Vet
.\mvnw.cmd spring-boot:run
```

Il backend e configurato per partire sulla porta `9020`.

Importante: controllare `src/main/resources/application.yml` prima di avviare il
backend. Attualmente contiene configurazioni specifiche dell'ambiente e segreti
che devono essere rimossi dal codice sorgente.

### Frontend

Dalla cartella del frontend:

```powershell
cd React\vetmanager-portal
npm install
npm run dev
```

Il server di sviluppo Vite inoltra le richieste `/api` verso:

```text
http://localhost:9020
```

### Comandi Utili Frontend

```powershell
npm run dev
npm run lint
npm run build
npm run preview
```

### Comandi Utili Backend

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

I test backend attualmente dipendono dalla configurazione Spring attiva, quindi
devono essere rivisti dopo l'aggiunta di un profilo di test dedicato.

## Scope Funzionale Attuale

### Autenticazione E Accesso

- Login con email e password.
- Flusso di registrazione per clienti.
- Richieste autenticate tramite JWT.
- Navigazione frontend basata sul ruolo e rotte protette.
- Sicurezza backend a livello metodo con annotazioni di ruolo e alcuni controlli
  manuali di proprieta.

### Area Cliente

- Visualizzazione dei propri animali.
- Aggiunta di un animale.
- Prenotazione di un appuntamento da slot disponibili.
- Visualizzazione e gestione appuntamenti.
- Schermate orientate a fatture e pagamenti.
- Visualizzazione della cartella clinica animale in sola lettura dove collegata.

### Area Receptionist

- Visualizzazione dashboard operativa.
- Gestione appuntamenti.
- Riprogrammazione o cancellazione appuntamenti.
- Invio notifiche di ritardo.
- Schermate orientate a clienti e pagamenti.

### Area Medico

- Visualizzazione dashboard e dati agenda.
- Visualizzazione registro animali.
- Apertura cartella clinica animale.
- Gestione dati animale, vaccinazioni e report visita.
- Gestione entita di configurazione come specie, razze, tipi vaccino, categorie
  visita e tipi visita.

### File, Mail E PDF

- Esistono template HTML per mail, fatture e ricette.
- Esiste un servizio di generazione PDF.
- Esiste la persistenza dei riferimenti file.
- Il ciclo completo di upload/download e audit deve ancora essere completato.

## Rischi Noti E Priorita Di Cleanup

1. Rimuovere i segreti dalla configurazione versionata e ruotare le credenziali
   esposte.
2. Aggiungere `application-example.yml`, `application-local.yml` e
   `application-test.yml`.
3. Fare in modo che i test backend usino un database locale/test sicuro.
4. Convertire lo schema database attivo in migrazioni versionate.
5. Sostituire i payload request/response basati su entita con DTO e validazioni.
6. Standardizzare nomi dei ruoli, path API e convenzioni di naming.
7. Completare i test di autorizzazione basati sui ruoli.
8. Aggiungere test frontend e integrare lint/build nel flusso normale.
9. Aggiungere documentazione per l'infrastruttura locale o un `docker-compose.yml`.
10. Allineare `TODO.md` dopo ogni fase di sviluppo completata.

## Documentazione

Ulteriori note di progetto sono archiviate in:

- `TODO.md`
- `CodeRequirements.md`
- `Documentazione/`
- `Mockup/`
- `DatiExcel/`

Parte della documentazione piu vecchia descrive ancora strutture pianificate o
mockup invece dello stato corrente del repository. Considerare questo README
come punto di ingresso aggiornato.
