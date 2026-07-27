import { apiFetch, readApiError } from './apiClient';

const VISIT_TYPES_API_BASE = '/api/tipiVisite';

export function normalizeVisitType(visitType) {
  if (!visitType) {
    return null;
  }

  return {
    id: visitType.id,
    nome: visitType.nome ?? visitType.tipologia ?? visitType.descrizione ?? '',
    durata: visitType.durata,
    prezzo: visitType.prezzo,
    dottore: visitType.dottore,
    raw: visitType,
  };
}

export async function fetchVisitTypes() {
  const response = await apiFetch(`${VISIT_TYPES_API_BASE}/ottieniTutti`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare i tipi di visita.');
    throw new Error(errorMessage || 'Impossibile recuperare i tipi di visita.');
  }

  const visitTypes = await response.json();
  const visitTypeList = Array.isArray(visitTypes) ? visitTypes : [visitTypes];

  return visitTypeList.map(normalizeVisitType).filter(Boolean);
}
