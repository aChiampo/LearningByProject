import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';

const API_BASE_URL = '/api/animali';

export async function fetchAnimalsByOwner(ownerId) {
  const response = await apiFetch(`${API_BASE_URL}/leggiPerUtente/${ownerId}`);

  if (!response.ok) {
    throw new Error(`Impossibile recuperare gli animali (status ${response.status})`);
  }

  return response.json();
}

export async function fetchAnimalOwners() {
  const response = await apiFetch('/api/utente/ottieniTutti');

  if (!response.ok) {
    throw new Error(`Impossibile recuperare i proprietari (status ${response.status})`);
  }

  return response.json();
}

export async function fetchSpecies() {
  const response = await apiFetch('/api/specie');

  if (!response.ok) {
    throw new Error(`Impossibile recuperare le specie (status ${response.status})`);
  }

  return response.json();
}

export async function fetchBreedsBySpecies(specieId) {
  const response = await apiFetch(`/api/razze/specie/${specieId}`);

  if (!response.ok) {
    throw new Error(`Impossibile recuperare le razze (status ${response.status})`);
  }

  return response.json();
}

export async function createAnimal(payload) {
  const response = await apiFetchWithPayload(`${API_BASE_URL}/crea`, [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, "Impossibile registrare l'animale.");
    throw new Error(errorMessage);
  }

  return response.json();
}

export async function sendAnimalEvaluationRequest(payload) {
  const response = await apiFetchWithPayload('/api/richieste-animali/valutazione', [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile inviare la richiesta di valutazione.');
    throw new Error(errorMessage);
  }

  return response.json();
}
