import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';
import { fetchAllAnimals } from './animalApi';
import { fetchUsers } from './userApi';
import { fetchVisitTypes } from './visitTypeApi';

const VISITS_API_BASE = '/api/visite';
const VISIT_PARAMS_ENDPOINT = `${VISITS_API_BASE}/params`;
const AVAILABLE_SLOTS_ENDPOINT = `${VISITS_API_BASE}/slot-disponibili`;
const BOOK_APPOINTMENT_ENDPOINT = `${VISITS_API_BASE}/prenota`;

export function getLocalStartOfToday() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}T00:00:00`;
}

function getFullName(person) {
  return [person?.nome, person?.cognome].filter(Boolean).join(' ');
}

function getReferenceId(value) {
  if (!value || typeof value !== 'object') {
    return value ?? null;
  }

  return value.id ?? value.ID ?? value.userId ?? value.idUtente ?? null;
}

function createLookup(items) {
  return new Map(
    items
      .map((item) => [getReferenceId(item), item])
      .filter(([id]) => id !== null && id !== undefined)
  );
}

function resolveReference(value, lookup) {
  const id = getReferenceId(value);
  const matchedValue = lookup?.get(id);

  if (!value || typeof value !== 'object') {
    return matchedValue ?? {};
  }

  return {
    ...matchedValue,
    ...value,
  };
}

function firstTextValue(...values) {
  return values.find((value) => typeof value === 'string' && value.trim()) ?? '';
}

function referenceLabel(label, value) {
  const id = getReferenceId(value);
  return id !== null && id !== undefined ? `${label} #${id}` : '';
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
    dateStyle: 'medium'
  }).format(date);
}

function normalizeVisitToAppointment(visit, lookups = {}) {
  const animal = resolveReference(visit.animale ?? visit.animal, lookups.animalsById);
  const visitType = resolveReference(visit.tipoVisita ?? visit.type, lookups.visitTypesById);
  const client = resolveReference(
    visit.cliente ?? visit.client ?? visit.proprietario ?? animal.proprietario ?? animal.utente,
    lookups.usersById
  );
  const doctor = resolveReference(visit.veterinario ?? visit.doctor ?? visitType.dottore, lookups.usersById);
  const visitDate = visit.dataVisita ?? visit.date ?? visit.data ?? '';
  const ownerName = firstTextValue(visit.ownerName, getFullName(client));

  return {
    id: visit.id,
    animalId: getReferenceId(visit.animale ?? visit.animal ?? visit.animalId ?? visit.animaleId),
    visitTypeId: getReferenceId(visit.tipoVisita ?? visit.type ?? visit.visitTypeId ?? visit.tipoVisitaId),
    doctorId: getReferenceId(visit.veterinario ?? visit.doctor ?? visitType.dottore ?? visit.doctorId ?? visit.veterinarioId),
    dataVisita: visitDate,
    animalName: firstTextValue(visit.animalName, visit.animaleNome, animal.nome, referenceLabel('Animale', visit.animale)),
    animalBreed: firstTextValue(visit.animalBreed, visit.animaleRazza, animal.razza?.nome, animal.razza),
    nome: firstTextValue(visit.nome, client.nome),
    cognome: firstTextValue(visit.cognome, client.cognome),
    ownerName: firstTextValue(ownerName, referenceLabel('Cliente', client)),
    visitType: firstTextValue(visit.visitType, visit.tipoVisitaNome, visitType.nome, referenceLabel('Tipo visita', visit.tipoVisita)),
    appointmentDate: visit.appointmentDate ?? formatAppointmentDate(visitDate),
    appointmentHour: visit.appointmentHour ?? formatAppointmentHour(visitDate),
    doctorName: firstTextValue(visit.doctorName, getFullName(doctor), referenceLabel('Medico', doctor)),
    status: visit.status ?? visit.stato ?? 'Programmato',
    note: visit.note ?? '',
    raw: visit
  };
}

async function loadAppointmentLookups() {
  const [animalsResult, visitTypesResult, usersResult] = await Promise.allSettled([
    fetchAllAnimals(),
    fetchVisitTypes(),
    fetchUsers(),
  ]);

  const animals = animalsResult.status === 'fulfilled' ? animalsResult.value : [];
  const visitTypes = visitTypesResult.status === 'fulfilled' ? visitTypesResult.value : [];
  const users = usersResult.status === 'fulfilled' ? usersResult.value : [];

  return {
    animalsById: createLookup(animals),
    visitTypesById: createLookup(visitTypes),
    usersById: createLookup(users),
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
  const lookups = await loadAppointmentLookups();

  return visitList.map((visit) => normalizeVisitToAppointment(visit, lookups));
}

export async function fetchAvailableSlots(payload) {
  const response = await apiFetchWithPayload(AVAILABLE_SLOTS_ENDPOINT, [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare gli slot disponibili.');
    throw new Error(errorMessage || 'Impossibile recuperare gli slot disponibili.');
  }

  const slots = await response.json();
  return Array.isArray(slots) ? slots : [slots];
}

export async function bookAppointment(payload) {
  const response = await apiFetchWithPayload(BOOK_APPOINTMENT_ENDPOINT, [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile prenotare la visita.');
    throw new Error(errorMessage || 'Impossibile prenotare la visita.');
  }

  return response.json();
}

export async function rescheduleAppointment(appointmentId, dataVisita) {
  const response = await apiFetchWithPayload(`${VISITS_API_BASE}/riprogramma/${appointmentId}`, [{ dataVisita }], {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile modificare l\'appuntamento.');
    throw new Error(errorMessage || 'Impossibile modificare l\'appuntamento.');
  }

  return response.json();
}

export async function deleteAppointment(appointmentId) {
  const response = await apiFetch(`${VISITS_API_BASE}/elimina/${appointmentId}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile cancellare l\'appuntamento.');
    throw new Error(errorMessage || 'Impossibile cancellare l\'appuntamento.');
  }
}

export async function sendDelayNotification(visitId, delayMinutes) {
  const response = await apiFetchWithPayload(`${VISITS_API_BASE}/notifica-ritardo`, [{
    visitaId: visitId,
    delayMinutes,
  }]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile inviare la notifica di ritardo.');
    throw new Error(errorMessage || 'Impossibile inviare la notifica di ritardo.');
  }
}
