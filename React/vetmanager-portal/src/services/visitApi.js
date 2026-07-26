import { apiFetch, readApiError } from './apiClient';

const VISITS_API_BASE = '/api/visite';

function getFullName(person) {
  return [person?.nome, person?.cognome].filter(Boolean).join(' ');
}

function getNestedName(value) {
  if (!value || typeof value !== 'object') {
    return value ?? '';
  }

  return value.nome ?? value.name ?? value.tipologia ?? '';
}

export function normalizeVisit(visit) {
  if (!visit) {
    return null;
  }

  return {
    id: visit.id,
    dataVisita: visit.dataVisita ?? visit.data ?? '',
    tipoVisita: getNestedName(visit.tipoVisita),
    tipoVisitaId: visit.tipoVisita?.id ?? visit.tipoVisita?.Id ?? '',
    animale: visit.animale,
    veterinario: visit.veterinario,
    veterinarioNome: getFullName(visit.veterinario),
    pagamento: visit.pagamento,
    note: visit.note ?? '',
    notaPrivata: visit.notaPrivata ?? '',
    stato: visit.stato ?? '',
    raw: visit,
  };
}

function normalizeVisitList(visits) {
  const visitList = Array.isArray(visits) ? visits : [visits];
  return visitList.map(normalizeVisit).filter(Boolean);
}

export async function fetchVisitsByAnimal(animalId) {
  const response = await apiFetch(`${VISITS_API_BASE}/ottieniPerAnimale/${animalId}`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, "Impossibile recuperare le visite dell'animale.");
    throw new Error(errorMessage);
  }

  return normalizeVisitList(await response.json());
}

export async function fetchVisitById(visitId) {
  const response = await apiFetch(`${VISITS_API_BASE}/ottieni/${visitId}`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare il dettaglio della visita.');
    throw new Error(errorMessage);
  }

  return normalizeVisit(await response.json());
}

export async function updateVisitNotes(visitId, note) {
  const queryParams = new URLSearchParams({ note });
  const response = await apiFetch(`${VISITS_API_BASE}/aggiornaNote/${visitId}?${queryParams.toString()}`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile salvare il report della visita.');
    throw new Error(errorMessage);
  }

  return normalizeVisit(await response.json());
}
