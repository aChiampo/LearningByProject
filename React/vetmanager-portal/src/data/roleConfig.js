export const PALETTES = ['aurora', 'lago', 'energia', 'iris', 'cielo', 'rosa'];

export const ROLE_CONFIG = {
  client: {
    label: "Area Cliente",
    userName: "Andrea Rossi",
    navigation: [
      { label: "I miei Animali", target: "dashboard-client" },
      { label: "Prenota Visita", target: "client-booking" },
      { label: "Fatture & Pagamenti", target: "client-billing" }
    ],
    // Le tre liste vuote che servono alla tua Dashboard
    animals: [], 
    appointments: [],
    billing: []
  },
  
  receptionist: {
    label: "Area Segreteria",
    userName: "Giulia Ferri",
    navigation: [
      { label: "Dashboard", target: "dashboard-receptionist" },
      { label: "Gestisci Appuntamenti", target: "view-appointment-receptionist" },
      { label: "Anagrafica Clienti", target: "view-clients-receptionist" },
      { label: "Gestione Cassa", target: "view-payment-receptionist" },
      { label: "Supporto FAQ", target: "faq-receptionist" }
    ],
    // Dati dei contatori statistici della home
    stats: {
      todayAppointmentsCount: 0,
      newClientsCount: 0,
      pendingAmount: 0
    },
    // Array dinamici pronti per Java
    allAppointments: [],
    allClients: [],
    pendingPayments: []
  },
  
  doctor: {
      label: "Area Clinica",
      userName: "Dott. Camillo Zampetti",
      navigation: [
        { label: "Agenda di Oggi", target: "dashboard-doctor" },
        { label: "Registro Animali", target: "view-animal-doctor" },
        { label: "Supporto FAQ", target: "faq-doctor" }
      ],
      // I dati dinamici che arriveranno da Spring Boot
      todayAppointments: [], // Sostituisce l'agenda fissa di oggi
      patientsRegistry: []   // Sostituisce il registro fisso di tutti gli animali
    },
  
  'super-admin': {
    label: "Amministratore",
    userName: "Direzione San Luca",
    navigation: [
      { label: "Log di Sistema", target: "dashboard-super-admin" },
      { label: "Gestione Personale", target: "admin-staff" },
      { label: "Configurazione Hub", target: "admin-settings" }
    ],
    // Array vuoto: pronto per tracciare le azioni reali sul server
    logs: [] 
  }
};