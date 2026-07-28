import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';

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

function getEntityId(value) {
  if (value && typeof value === 'object') {
    return value.id ?? value.Id ?? null;
  }

  return value ?? null;
}

function createVisitReference(value, fallbackId) {
  const id = getEntityId(value) ?? fallbackId;
  return id == null ? null : { id };
}

function buildCompleteVisitPayload(visit, note) {
  const rawVisit = visit?.raw ?? visit ?? {};

  return {
    id: rawVisit.id ?? visit?.id,
    animale: createVisitReference(rawVisit.animale ?? visit?.animale, visit?.animale?.id),
    tipoVisita: createVisitReference(rawVisit.tipoVisita, visit?.tipoVisitaId),
    veterinario: createVisitReference(rawVisit.veterinario ?? visit?.veterinario, visit?.veterinario?.id),
    dataVisita: rawVisit.dataVisita ?? visit?.dataVisita,
    pagamento: rawVisit.pagamento ? createVisitReference(rawVisit.pagamento, visit?.pagamento?.id) : null,
    note,
    notaPrivata: rawVisit.notaPrivata ?? visit?.notaPrivata ?? null,
  };
}

export async function completeVisitReport(visit, note) {
  const response = await apiFetchWithPayload(`${VISITS_API_BASE}/chiudivisita`, [
    buildCompleteVisitPayload(visit, note),
  ]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile completare il report della visita.');
    throw new Error(errorMessage);
  }

  return normalizeVisit(await response.json());
}
