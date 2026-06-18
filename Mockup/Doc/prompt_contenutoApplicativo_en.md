# VetManager Portal Mockup

## Context

Create a clickable web application mockup for **VetManager**.

Clinic information:

- Main office: Bergamo Alta, IT
- Doctor: Dott. Camillo Zampetti
- Patients: dogs, cats, and small animals

The portal supports four user roles with different navigation and page access:

- Super Admin
- Doctor/Veterinarian
- Receptionist
- Client

## Deliverable

Build the mockup with HTML, CSS, and JavaScript. It must work as a static website without a backend.

- Use hash-based navigation between screens.
- Keep all visible application content in Italian.
- Use semantic and accessible HTML where practical.
- Keep the mockup responsive and prevent horizontal page overflow.
- Preserve a usable HTML navigation fallback if JavaScript is unavailable.
- Use JavaScript to centralize repeated role configuration, navigation generation, and contextual help behavior.

## Global Header

The sticky header must contain:

- The VetManager brand and a link to the homepage.
- A presentation-only role switcher with links for Super Admin, Veterinario, Reception, and Cliente.
- A presentation-only palette selector.

Available palettes:

- Aurora: teal and coral
- Lago: blue and mint
- Energia: red and gold
- Iris: violet and lilac
- Cielo: slate and cyan
- Rosa: amber and rose

The selected palette must be clearly visible across the interface, including the header, sidebar, page-title accent, buttons, panels, cards, calendar, FAQ elements, form focus states, and page background.

## Application Layout

After selecting a role, all application screens use a left sidebar on desktop.

- The sidebar must remain consistent across every screen belonging to the same role.
- Show the role and current username at the top of the sidebar.
- Keep the sidebar sticky on desktop.
- Move it above the content on smaller screens.
- Do not allow grids, calendars, appointment rows, or the header to make the document wider than the viewport.

Current mockup users:

- Super Admin: Admin Aurora
- Doctor: Dott. Camillo Zampetti
- Receptionist: Giulia Ferri
- Client: Andrea Rossi

## Role Navigation

### Super Admin

- Dashboard: `#dashboard-super-admin`
- FAQ: `#faq-super-admin`

### Doctor

- Dashboard: `#dashboard-doctor`
- Gestisci animali: `#view-animal-doctor`
- FAQ: `#faq-doctor`

### Receptionist

- Dashboard: `#dashboard-receptionist`
- Gestisci appuntamenti: `#view-appointment-receptionist`
- Gestisci clienti: `#view-clients-receptionist`
- Gestisci pagamenti: `#view-payment-receptionist`
- FAQ: `#faq-receptionist`

### Client

- Dashboard: `#dashboard-client`
- Prenota appuntamento: `#booking-client`
- Gestisci pagamenti: `#view-payment-client`
- FAQ: `#faq-client`

## Contextual Help

Show one floating action at the bottom-right of role-specific screens.

- Outside an FAQ screen, every role sees `AIUTO`, linked to that role's FAQ.
- Inside the Client FAQ, the floating action is hidden.
- Inside the Super Admin, Doctor, or Receptionist FAQ, it becomes `APRI TICKET ASSISTENZA`.
- Do not show the floating action on the public homepage.

The assistance-ticket action is currently a mockup state and does not require a backend ticket workflow.

## Pages

### Homepage - `#homepage`

Show:

- The clinic name and Bergamo Alta location.
- A headline describing appointments and medical records in one portal.
- Registration and login buttons linking to the Client dashboard.
- Dott. Camillo Zampetti and the main office location.
- Opening hours, emergency availability, and specialties.

### Super Admin Dashboard - `#dashboard-super-admin`

Show a placeholder explaining that operational Super Admin functions are still to be implemented.

### Doctor Dashboard - `#dashboard-doctor`

Show, in order:

- Page title: Agenda clinica.
- Weekly calendar with appointments.
- Previous week, next week, and new event controls.
- Animal search using the placeholder `Cerca per: proprietario o nome animale`.
- Animal result cards containing animal name, client name, breed, and a medical-record button.
- Upcoming appointments containing animal, visit type, start/end time, create-report action, and delete action.

### Receptionist Dashboard - `#dashboard-receptionist`

Show:

- Page title: Operativita giornaliera.
- Weekly appointment calendar.
- Previous week, next week, and new event controls.
- Upcoming appointments with edit and delete actions.

### Client Dashboard - `#dashboard-client`

Show:

- Upcoming appointments with edit and delete actions.
- An animal section with an add-animal button.
- Animal cards containing name, breed, sex, open-record action, and book-visit action.

### Client Medical Record - `#view-record-client`

Show:

- Animal data: name, surname, breed, birth date, sex, weight, and microchip.
- Vaccination list.
- Visit-report cards linked to the Client Visit Report.
- A clinical-document upload area.

### Doctor Medical Record - `#view-record-doctor`

Show:

- Owner information.
- Vaccination list.
- Animal data: name, surname, breed, birth date, sex, weight, and microchip.
- Edit-data action.
- Visit-report cards linked to the Doctor Visit Report.

### Client Visit Report - `#view-visit-report-client`

Show the visit type, date, public note, and attachments.

### Doctor Visit Report - `#view-visit-report-doctor`

Show the visit type, date, public note, private note, attachments, and edit-report action.

### Doctor Animal Search - `#view-animal-doctor`

Show:

- Search by owner or animal name.
- Animal cards containing animal name, client name, breed, and medical-record action.

### Receptionist Appointments - `#view-appointment-receptionist`

Show:

- Search/filter by owner or animal name.
- Booked appointments with edit and delete actions.
- A button linking to the Receptionist Booking screen.

### Receptionist Clients - `#view-clients-receptionist`

Show:

- Search/filter by owner or animal name.
- Client cards with edit-data, payments, and receptionist booking actions.

### Client Booking - `#booking-client`

Keep the Client sidebar visible. Show a form with:

- Animal selection.
- Visit-type selection.
- Doctor selection containing Dott. Camillo Zampetti.
- Available-time-slot selection.
- Confirm-request action.

### Receptionist Booking - `#booking-receptionist`

Keep the Receptionist sidebar visible. Show a form with:

- Client selection.
- Animal selection.
- Visit-type selection.
- Doctor selection containing Dott. Camillo Zampetti.
- Available-time-slot selection.
- Confirm-booking action.

### Client Payments - `#view-payment-client`

Show:

- Pending payments with a `Paga` action.
- Previous payments with a `Scarica` invoice action.

### Receptionist Payments - `#view-payment-receptionist`

Show:

- Search/filter by owner.
- Pending payments with `Invia promemoria` and `Paga` actions for each client.
- An `Invia promemoria a tutti` action.
- Previous payments with individual `Scarica` actions.
- A `Scarica tutte` action.

## FAQ Pages

Each FAQ page must use a list of native expandable elements, such as `<details>` and `<summary>`. Instructions must be short and appropriate for the selected role.

### Super Admin FAQ - `#faq-super-admin`

Explain how to:

- Check the current portal implementation status.
- Switch between presentation roles.
- Change the interface palette.

### Doctor FAQ - `#faq-doctor`

Explain how to:

- Consult the clinical calendar.
- Find an animal's medical record.
- Create or edit a visit report.
- Update clinical data.

### Receptionist FAQ - `#faq-receptionist`

Explain how to:

- Book a new appointment.
- Edit client data.
- Manage a pending payment or send a reminder.
- Edit or delete an appointment.

### Client FAQ - `#faq-client`

Explain how to:

- Book a visit.
- Open an animal's medical record.
- Upload a clinical document.
- Pay for a visit.

## Interaction Notes

- Most mockup actions can link back to their current screen or to the relevant destination screen.
- Forms and destructive actions do not need persistence or backend behavior.
- Navigation must never change to another role while moving between screens.
- Client and Receptionist booking flows must use separate screens so their sidebars remain role-correct.
- JavaScript should define role names, usernames, navigation links, FAQ destinations, and contextual-help state in one shared configuration.
