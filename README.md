# Chi siamo

breve presentazione azienda


## User Requirements:

Il Dott. Camillo Zampetti gestisce uno studio veterinario a Bergamo Alta.
Ha circa 280 pazienti attivi tra cani, gatti e piccoli animali.
Attualmente perde molto tempo nella gestione di attività ripetitive:
telefonate per prenotazioni, richieste via WhatsApp, ricette smarrite,
informazioni cliniche incomplete e libretti vaccinali cartacei.

Vorrebbe uno strumento digitale semplice che aiuti lui, I receptionist e i proprietari degli animali a gestire meglio le informazioni principali.

## Componente Tecnica
### Stack applicativo


| Livello             | Tecnologia                     |
| ------------------- | ------------------------------ |
| Frontend            | React                          |
| Backend             | Spring Boot 3.5.x, Java 21 LTS |
| Database            | PostgreSQL                     |
| Versionamento       | GitHub                         |
| Infrastruttura Host | Cloud gestito (AWS) consigliato|
| Database Hosting    | To be implemented              |

# Descrizione Progetto

 Il progetto deve essere strutturato come una web application.

 Deve essere accessibile dal web presentando una home page dalla quale si può essere reindirizzati ad altre pagine dopo essersi autenticati. In base al ruolo saranno accessibili diverse funzionalità.

 Gli utilizzatori saranno di tre tipi:
 - Il dottore/veterinario proprietario dello studio
 - La receptionist 
 - I clienti dello studio ovvero i proprietari degli animali

## Tasks and Functionalities

### Authentication & Access
- Sign in / sign out
- Access role-based dashboards (Client, Receptionist, Doctor, Admin)

### Client (Patient) Actions
- Browse services and view available visit slots
- Book an appointment
- View, modify, or cancel own appointments
- View personal medical record (read-only)
- Pay for appointments and download invoices
- Receive confirmations and reminders (notifications)

### Receptionist Actions
- View receptionist dashboard with incoming appointments
- Create, modify, and cancel appointments for clients
- Confirm bookings and manage waiting lists
- Record payments and reconcile invoices

### Doctor Actions
- View own dashboard and today's/filtered appointments
- Open and update patient medical charts
- Add visit notes and prescriptions
- Reschedule or close appointments

### Records & Files
- Upload and download clinical documents and attachments
- Edit patient charts (role-based permissions)
- View file history and versioning (audit trail)

### Payments & Billing
- Mark invoices as paid or unpaid
- Trigger payment requests and process receipts
- Export or download invoices and payment reports

### Administration & Reporting
- Manage user roles and permissions
- Configure services, availability, and schedules
- Generate reports (appointments, payments, activity)

### Notifications & Communication
- Send confirmations, reminders, and status updates to users
- Allow users to confirm or respond to messages

### Miscellaneous
- Search and filter appointments, patients, and records
- Long-term storage and backup 
- Reporting and statistics









possibilità di hostare il server in locale oppure comprare un servizio cloud (AWS)
