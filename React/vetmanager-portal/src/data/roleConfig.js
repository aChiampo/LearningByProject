export const BACKEND_ROLE_TO_FRONTEND_ROLE = {
  CLIENTE: 'client',
  VETERINARIO: 'doctor',
  RECEPTIONIST: 'receptionist',
  ADMIN: 'super-admin',
};

export function normalizeRole(role) {
  return BACKEND_ROLE_TO_FRONTEND_ROLE[role] ?? role ?? 'client';
}

export const ROLE_CONFIG = {
  client: {
    label: "Area Cliente",
    userName: "Andrea Rossi",
    dashboard: "/client/dashboard",
    faq: "/client/faq",
    navigation: [
      { label: "I miei Animali", target: "/client/dashboard" },
      { label: "Profilo", target: "/client/profile" },
      { label: "Prenota Visita", target: "/client/booking" },
      { label: "Fatture & Pagamenti", target: "/client/billing" },
      { label: "Supporto FAQ", target: "/client/faq" }
    ]
  },
  
  receptionist: {
    label: "Area Segreteria",
    userName: "Giulia Ferri",
    dashboard: "/receptionist/dashboard",
    faq: "/receptionist/faq",
    navigation: [
      { label: "Dashboard", target: "/receptionist/dashboard" },
      { label: "Profilo", target: "/receptionist/profile" },
      { label: "Gestisci Appuntamenti", target: "/receptionist/appointments" },
      { label: "Anagrafica Clienti", target: "/receptionist/clients" },
      { label: "Gestione Cassa", target: "/receptionist/payments" },
      { label: "Supporto FAQ", target: "/receptionist/faq" }
    ]
  },
  
  doctor: {
      label: "Area Clinica",
      userName: "Dott. Camillo Zampetti",
      dashboard: "/doctor/dashboard",
      faq: "/doctor/faq",
      navigation: [
        { label: "Agenda di Oggi", target: "/doctor/dashboard" },
        { label: "Profilo", target: "/doctor/profile" },
        { label: "Registro Animali", target: "/doctor/animals" },
        { label: "Gestione", target: "/doctor/management" },
        { label: "Supporto FAQ", target: "/doctor/faq" }
      ]
    },
  
  'super-admin': {
    label: "Amministratore",
    userName: "Direzione San Luca",
    dashboard: "/super-admin/dashboard",
    faq: "/super-admin/faq",
    navigation: [
      { label: "Log di Sistema", target: "/super-admin/dashboard" },
      { label: "Profilo", target: "/super-admin/profile" },
      { label: "Gestione Personale", target: "/super-admin/staff" },
      { label: "Configurazione Hub", target: "/super-admin/settings" },
      { label: "Supporto FAQ", target: "/super-admin/faq" }
    ]
  }
};
