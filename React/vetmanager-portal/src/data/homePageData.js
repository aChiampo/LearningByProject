export const HOME_HIGHLIGHTS = [
  {
    title: 'Visite personalizzate',
    description: 'Ogni animale riceve attenzione dedicata',
  },
  {
    title: 'Prevenzione e follow-up',
    description: 'Vaccini, controlli e richiami programmati',
  },
  {
    title: 'Supporto vicino',
    description: 'Indicazioni chiare prima e dopo la visita',
  },
];

export const HOME_SERVICES = [
  {
    number: '01',
    title: 'Visite e controlli',
    description: "Check-up, consulti clinici e valutazioni mirate per mantenere l'animale in salute.",
  },
  {
    number: '02',
    title: 'Vaccinazioni e prevenzione',
    description: 'Piani vaccinali, richiami e indicazioni pratiche per prevenire i problemi piu comuni.',
  },
  {
    number: '03',
    title: 'Gestione appuntamenti',
    description: 'Prenotazioni, promemoria e documenti raccolti in un percorso semplice da seguire.',
  },
];

export const CLINIC_HOURS = [
  {
    label: 'Lunedi - Venerdi',
    value: '08:30 - 19:30',
  },
  {
    label: 'Sabato',
    value: '09:00 - 13:00',
  },
  {
    label: 'Urgenze',
    value: 'Su prenotazione',
  },
];

export const BOOKING_OPTIONS = [
  {
    type: 'login',
    eyebrow: 'Hai gia un profilo',
    title: 'Area pazienti',
    description: 'Accedi per consultare appuntamenti, documenti e pagamenti.',
  },
  {
    type: 'registration',
    path: '/registrati',
    eyebrow: 'Nuovo paziente',
    title: 'Crea un profilo',
    description: 'Registra i tuoi dati per gestire gli appuntamenti piu velocemente.',
  },
  {
    type: 'first-appointment',
    path: '/first-appointment',
    eyebrow: 'Prima visita',
    title: 'Richiesta iniziale',
    description: "Invia una richiesta senza avere ancora un profilo nell'area pazienti.",
  },
];

export const INITIAL_REGISTRATION_FORM = {
  nome: '',
  cognome: '',
  email: '',
  telefono: '',
  password: '',
};
