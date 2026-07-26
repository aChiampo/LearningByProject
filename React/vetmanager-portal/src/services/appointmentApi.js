import { apiFetchWithPayload, readApiError } from './apiClient';

const VISITS_API_BASE = '/api/visite';
const VISIT_PARAMS_ENDPOINT = `${VISITS_API_BASE}/params`;

function getFullName(person) {
  return [person?.nome, person?.cognome].filter(Boolean).join(' ');
}

function formatAppointmentHour(dateTime) {
  if (!dateTime) {
    return '';
  }

  const date = new Date(dateTime);

  if (Number.isNaN(date.getTime())) {
    return '';
  }

  return new Intl.DateTimeFormat('it-IT', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

function formatAppointmentDate(dateTime) {
  if (!dateTime) {
    return '';
  }

  const date = new Date(dateTime);

  if (Number.isNaN(date.getTime())) {
    return dateTime;
  }

  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(date);
}

function normalizeVisitToAppointment(visit) {
  const client = visit.cliente ?? visit.client ?? visit.proprietario ?? visit.animale?.utente ?? {};
  const doctor = visit.veterinario ?? visit.doctor ?? visit.tipoVisita?.dottore ?? {};
  const visitDate = visit.dataVisita ?? visit.date ?? visit.data ?? '';

  return {
    id: visit.id,
    animalName: visit.animalName ?? visit.animaleNome ?? visit.animale?.nome ?? '',
    animalBreed: visit.animalBreed ?? visit.animaleRazza ?? visit.animale?.razza?.nome ?? visit.animale?.razza ?? '',
    nome: visit.nome ?? client.nome ?? '',
    cognome: visit.cognome ?? client.cognome ?? '',
    ownerName: visit.ownerName ?? getFullName(client),
    visitType: visit.visitType ?? visit.tipoVisitaNome ?? visit.tipoVisita?.nome ?? '',
    appointmentDate: visit.appointmentDate ?? formatAppointmentDate(visitDate),
    appointmentHour: visit.appointmentHour ?? formatAppointmentHour(visitDate),
    doctorName: visit.doctorName ?? getFullName(doctor),
    status: visit.status ?? visit.stato ?? 'Programmato',
    raw: visit
  };
}

export async function fetchAppointments(params = {}) {
  const response = await apiFetchWithPayload(VISIT_PARAMS_ENDPOINT, [params]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare gli appuntamenti.');
    throw new Error(errorMessage || 'Impossibile recuperare gli appuntamenti.');
  }

  const visits = await response.json();
  const visitList = Array.isArray(visits) ? visits : [visits];

  return visitList.map(normalizeVisitToAppointment);
}
