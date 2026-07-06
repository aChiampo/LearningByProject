# React Simple Guide and VetManager Components

This note explains, in simple terms, how React works and how it can be used to transform the current VetManager mockup into a real web application connected to a Spring Boot backend.

React is often called a framework, but technically it is a JavaScript library for building user interfaces. In a real project, React is usually combined with tools such as Vite, React Router, and an API layer. Together, these tools behave like a complete frontend framework.

**Index**

- [How React Works, Simply](#how-react-works-simply)
- [Suggested React Project Structure](#suggested-react-project-structure)
- [Retrieving and Sending Data from Spring Boot](#retrieving-and-sending-data-from-spring-boot)
- [Components Needed for the VetManager Web App](#components-needed-for-the-vetmanager-web-app)
- [Calendar Component Decision Guide](#calendar-component-decision-guide)
- [Sources](#sources)

## How React Works Simply

React lets you build the interface using small reusable pieces called components.

A component is a JavaScript function that returns UI markup:

```jsx
function UserCard({ name, email }) {
  return (
    <article className="card">
      <h3>{name}</h3>
      <p>{email}</p>
    </article>
  );
}
```

The most important React ideas are:

- Component: one reusable piece of interface, such as a button, sidebar, calendar, card, form, or whole page.
- Props: values passed from a parent component to a child component.
- State: data that can change while the user uses the app, such as selected date, logged user, form values, or search text.
- Events: functions called when the user clicks, types, submits a form, selects a date, and so on.
- Rendering: React updates the visible interface when props or state change.
- Lists: arrays of data can become repeated UI elements, such as appointment rows or animal cards.
- Conditional rendering: React can show different UI depending on role, status, loading state, or permissions.

Example:

```jsx
import { useState } from "react";

function CounterButton() {
  const [count, setCount] = useState(0);

  return (
    <button onClick={() => setCount(count + 1)}>
      Clicked {count} times
    </button>
  );
}
```

When `setCount` is called, React runs the component again and updates the screen.

## Suggested React Project Structure

For VetManager, a good first structure could be:

```text
src/
  App.jsx
  main.jsx
  routes/
    AppRouter.jsx
    ProtectedRoute.jsx
  layouts/
    PublicLayout.jsx
    AppLayout.jsx
  components/
    common/
    appointments/
    animals/
    payments/
    records/
    faq/
    forms/
  pages/
    public/
    doctor/
    receptionist/
    client/
    super-admin/
  services/
    apiClient.js
    utentiService.js
    appointmentsService.js
    animalsService.js
  context/
    AuthContext.jsx
    ThemeContext.jsx
  data/
    roleConfig.js
```

The current mockup keeps all screens in one HTML file and changes screen using hash links. In React, each screen should become a page component and routing should decide which page is visible.

## Retrieving and Sending Data from Spring Boot

The React frontend should not talk directly to the database. It should call your Spring Boot REST API.

Typical data flow:

```text
React component
  -> frontend service function
  -> HTTP request with fetch or Axios
  -> Spring Boot controller
  -> service/repository/database
  -> JSON response
  -> React state update
  -> UI refresh
```

For now, `fetch` is enough. Later, if the app grows, you can use Axios or TanStack Query for caching, loading states, retries, and cleaner request management.

### Basic API Client

Create one reusable API helper:

```js
const API_BASE_URL = "http://localhost:9020";

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
```

### Example Service for `/api/Utenti/`

Use the exact endpoint requested here: `/api/Utenti/`.

```js
import { apiRequest } from "./apiClient";

export function getUtenti() {
  return apiRequest("/api/Utenti/");
}

export function getUtenteById(id) {
  return apiRequest(`/api/Utenti/${id}`);
}

export function createUtente(newUtente) {
  return apiRequest("/api/Utenti/", {
    method: "POST",
    body: JSON.stringify(newUtente),
  });
}

export function updateUtente(id, updatedUtente) {
  return apiRequest(`/api/Utenti/${id}`, {
    method: "PUT",
    body: JSON.stringify(updatedUtente),
  });
}

export function deleteUtente(id) {
  return apiRequest(`/api/Utenti/${id}`, {
    method: "DELETE",
  });
}
```

### Retrieve Users in a React Component

```jsx
import { useEffect, useState } from "react";
import { getUtenti } from "../services/utentiService";

function UtentiList() {
  const [utenti, setUtenti] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let ignore = false;

    async function loadUtenti() {
      try {
        setLoading(true);
        const data = await getUtenti();

        if (!ignore) {
          setUtenti(data);
        }
      } catch (err) {
        if (!ignore) {
          setError(err.message);
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    loadUtenti();

    return () => {
      ignore = true;
    };
  }, []);

  if (loading) return <p>Loading users...</p>;
  if (error) return <p>{error}</p>;

  return (
    <ul>
      {utenti.map((utente) => (
        <li key={utente.id}>
          {utente.nome} {utente.cognome}
        </li>
      ))}
    </ul>
  );
}
```

### Send a New User to Spring Boot

```jsx
import { useState } from "react";
import { createUtente } from "../services/utentiService";

function CreateUtenteForm() {
  const [formData, setFormData] = useState({
    nome: "",
    cognome: "",
    email: "",
  });

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    await createUtente(formData);
  }

  return (
    <form onSubmit={handleSubmit}>
      <input name="nome" value={formData.nome} onChange={handleChange} />
      <input name="cognome" value={formData.cognome} onChange={handleChange} />
      <input name="email" value={formData.email} onChange={handleChange} />
      <button type="submit">Create user</button>
    </form>
  );
}
```

### Spring Boot Controller Example

Your backend could expose `/api/Utenti/` like this:

```java
package com.WW.controllers;

import com.WW.models.Utente;
import com.WW.repositories.UtenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/Utenti")
@CrossOrigin(origins = "http://localhost:5173")
public class UtenteController {

    private final UtenteRepository utenteRepository;

    public UtenteController(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/")
    public List<Utente> findAll() {
        return utenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Utente findById(@PathVariable Integer id) {
        return utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente create(@RequestBody Utente utente) {
        return utenteRepository.save(utente);
    }

    @PutMapping("/{id}")
    public Utente update(@PathVariable Integer id, @RequestBody Utente updatedUtente) {
        Utente existing = utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existing.setNome(updatedUtente.getNome());
        existing.setCognome(updatedUtente.getCognome());
        existing.setEmail(updatedUtente.getEmail());

        return utenteRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        utenteRepository.deleteById(id);
    }
}
```

Important notes:

- React usually runs on `http://localhost:5173` during development.
- Spring Boot may run on `http://localhost:9020` in this project.
- Because they are different origins, Spring Boot must allow CORS.
- In production, the frontend and backend URLs may change, so keep the API base URL in an environment variable.

## Components Needed for the VetManager Web App

The current mockup can become these React components.

### AppRouter

Controls which page is visible. It replaces the current hash navigation. It can support public routes, private authenticated routes, and role-protected routes.

Customizable by changing route paths, role permissions, fallback pages, and redirect behavior.

### PublicLayout

Used for public pages such as homepage, login, registration, first appointment request, and staff access.

Customizable with different header visibility, hero image, public navigation links, and footer content.

### AppLayout

Used after login. It contains the shared sidebar, page container, contextual help button, and role-based navigation.

Customizable by role, sidebar width, page title style, responsive behavior, and active menu state.

### Header

Shows the clinic brand, public navigation, and palette selector.

Customizable with logo, clinic name, public links, theme palette options, and sticky behavior.

### Sidebar

Shows role name, current user, and navigation links. The mockup already has different navigation for doctor, receptionist, client, and super admin.

Customizable through a `roleConfig` object containing labels, usernames, paths, and allowed menu items.

### RoleNavigation

Builds the menu from role configuration instead of hardcoding links in every page.

Customizable by adding new roles, changing labels, hiding unavailable pages, or marking active links.

### ThemePaletteSelector(DO NOT INCLUDE)

Lets the user choose a color palette. In React, the selected palette can be stored in state, context, or local storage.

Customizable with palette names, CSS variables, default palette, and persistence.

### HelpAction

Shows the floating `AIUTO` or `APRI TICKET ASSISTENZA` action depending on role and current page.

Customizable with role rules, hidden pages, destination route, text, and future ticket creation behavior.

### PageTitle

Standard component for page eyebrow, title, and optional action buttons.

Customizable with title text, subtitle, icon, action slot, and visual accent color.

### Panel

Reusable container for grouped content such as calendar blocks, forms, FAQ lists, payments, or profile details.

Customizable with title, actions, padding, border style, grid layout, and loading state.

### Button

Reusable button/link style used for primary, secondary, small, and danger actions.

Customizable with variant, size, disabled state, loading state, icon, and whether it renders as `<button>` or route link.

### DataCard

Generic card used for animals, clients, visit reports, and profile summaries.

Customizable with title, metadata rows, actions, status badge, and compact/full display modes.

### SearchForm

Used to search animals, clients, appointments, or payments.

Customizable with placeholder, filters, submit behavior, debounce, clear button, and advanced filters.

### LoginForm

Handles username/password input and submit action.

Customizable with remember-me checkbox, validation messages, password reset link, demo login buttons, and backend login endpoint.

### RegistrationForm

Creates a new client profile.

Customizable with required fields, validation, password rules, privacy checkbox, and success redirect.

### FirstAppointmentRequestForm

Allows a new patient to request a first appointment without an account.

Customizable with species options, visit reasons, notes, contact fields, and submit endpoint.

### DashboardPage

Generic page structure for doctor, receptionist, client, and super admin dashboards.

Customizable by passing different widgets according to role.

### AppointmentCalendar

Shows appointments by day or week. This is one of the most important components for the doctor and receptionist dashboards.

Customizable with view type, selected date, visible range, events, event colors, role-specific actions, appointment click behavior, drag-and-drop, working hours, unavailable slots, locale, and time zone.

Recommended options:

- Custom simple calendar: best for the first version if you only need the mockup behavior, weekly columns, and appointment labels. It is easier to style exactly like your design, but you must build date logic, event positioning, navigation, accessibility, and edge cases yourself.
- FullCalendar React: best if you need a complete scheduling component with month/week/day views, event callbacks, plugins, interaction support, and strong customization through props. Good for doctor/receptionist agenda management.
- React Big Calendar: good if you want a Google Calendar or Outlook-style calendar built specifically for React. It needs a date localizer such as date-fns, Day.js, Moment, or Globalize. Good for week/day appointment views.
- MUI X DateCalendar/DatePicker: good for choosing a date in booking forms, but not enough by itself for a full appointment agenda. It is useful beside a time-slot selector.

For VetManager, a practical choice is:

- Use FullCalendar or React Big Calendar for doctor and receptionist dashboards.
- Use MUI DatePicker or a custom date selector for client/receptionist booking forms.
- Start with a custom simple calendar only if you want to avoid dependencies during the first learning phase.

### CalendarToolbar

Contains previous week, next week, today, and new event controls.

Customizable with visible buttons, current range label, role-specific actions, and disabled states.

### AppointmentList

Shows upcoming appointments as rows or cards.

Customizable with columns, date format, action buttons, empty state, sorting, filtering, and role-specific actions such as edit, delete, create report, or book visit.

### AppointmentRow

Single appointment item containing animal, owner, visit type, date/time, status, and actions.

Customizable with role-specific buttons, status color, compact mode, and click behavior.

### BookingForm

Used by clients and receptionists to create or request an appointment.

Customizable with fields for client, animal, visit type, doctor, date, available slot, notes, validation, and submit behavior.

### TimeSlotPicker

Shows available appointment times for the selected doctor and date.

Customizable with slot duration, disabled slots, working hours, emergency slots, selected state, and loading state.

### AnimalSearch

Lets doctor or receptionist search by owner or animal name.

Customizable with search placeholder, filters, debounce, result count, and empty state.

### AnimalCard

Shows animal name, breed, sex, owner, and actions such as open record or book visit.

Customizable with visible fields, action buttons, role-specific metadata, and status badges.

### MedicalRecordPage

Shows the animal medical record. Client and doctor versions share much of the structure, but the doctor sees private clinical notes and edit actions.

Customizable by role, visible sections, editable mode, and permissions.

### AnimalDataPanel

Shows animal personal data such as name, breed, birth date, sex, weight, and microchip.

Customizable with field list, edit mode, validation, and layout.

### OwnerInfoPanel

Shows owner information for doctor and receptionist contexts.

Customizable with contact fields, privacy visibility, edit action, and quick links.

### VaccinationTable

Shows vaccinations and related documentation.

Customizable with columns, status badges, upload links, sorting, and next reminder date.

### VisitReportCard

Shows one visit report summary and links to the detailed report.

Customizable with public/private note visibility, attachments, date format, and edit button.

### VisitReportPage

Shows visit type, date, public note, private note for doctors, attachments, and edit action.

Customizable by role, permissions, editable fields, and attachment preview.

### DocumentUpload

Allows clients or staff to upload clinical documents.

Customizable with accepted file types, max size, drag-and-drop, progress state, and upload endpoint.

### ClientsList

Used by receptionist to search and manage client profiles.

Customizable with filters, client cards, payment links, booking shortcut, and edit actions.

### ClientCard

Shows client name, animals, contact data, and quick actions.

Customizable with visible contact fields, action buttons, risk/payment status, and compact mode.

### PaymentList

Groups payments by pending and completed status.

Customizable with filters, status groups, totals, reminder actions, and role-specific actions.

### PaymentRow

Single payment item showing description, amount, status, and action.

Customizable with currency, status badge, action buttons, and invoice download behavior.

### ProfilePage

Shows profile data for super admin, doctor, receptionist, or client.

Customizable with role-specific fields, editable mode, security actions, and notification preferences.

### ProfileDetailsPanel

Reusable profile information section.

Customizable with fields, labels, sensitive data visibility, and edit button.

### FaqList

Shows FAQ questions using expandable details.

Customizable with role-specific FAQ items, default open question, search, categories, and ticket link.

### TicketAction

Future component for opening an assistance ticket from staff FAQ pages.

Customizable with ticket categories, priority, message field, attachments, and backend endpoint.

### EmptyState

Shown when there are no appointments, no animals, no payments, or no search results.

Customizable with title, message, action button, and illustration/icon.

### LoadingState

Shown while data is being loaded from Spring Boot.

Customizable with spinner, skeleton layout, loading text, and page-specific placeholder.

### ErrorState

Shown when an API request fails.

Customizable with error message, retry button, support link, and technical details in development mode.

## Calendar Component Decision Guide

Calendar choice matters because appointments are central to the app.



Choose FullCalendar React if:

- You want a mature appointment calendar with many views and plugins.
- You need interactions like date click, event click, drag, resize, and external event sources.
- You want one strong calendar for doctor and receptionist agendas.

Choose React Big Calendar if:

- You want a calendar that feels similar to Google Calendar or Outlook.
- You prefer a React-focused API.
- You are comfortable setting up a localizer such as date-fns or Day.js.

![alt](assets/docImages/calendar-comparison.jpg)

Suggested first implementation:

```text
DoctorDashboard
  AppointmentCalendar
  CalendarToolbar
  AppointmentList

ReceptionistDashboard
  AppointmentCalendar
  CalendarToolbar
  AppointmentList

ClientBookingPage
  BookingForm
  DatePicker
  TimeSlotPicker
```

## Sources

- React Quick Start: https://react.dev/learn
- React `useEffect` and data fetching notes: https://react.dev/reference/react/useEffect
- FullCalendar React docs: https://fullcalendar.io/docs/react
- React Big Calendar repository/docs: https://github.com/bigcalendar/react-big-calendar