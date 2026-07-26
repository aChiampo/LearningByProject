import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';

const API_BASE_URL = '/api/animali';

function getNestedName(value) {
  if (!value || typeof value !== 'object') {
    return value ?? '';
  }

  return value.nome ?? value.name ?? value.tipologia ?? value.razza ?? '';
}

export function normalizeAnimal(animal) {
  if (!animal) {
    return null;
  }

  return {
    id: animal.id,
    nome: animal.nome ?? '',
    specie: getNestedName(animal.specie),
    razza: getNestedName(animal.razza),
    sesso: animal.sesso ?? '',
    dataNascita: animal.dataNascita,
    peso: animal.peso ?? '',
    microchip: animal.microchip ?? '',
    note: animal.note ?? '',
    proprietario: animal.proprietario ?? animal.utente,
    raw: animal,
  };
}

function normalizeAnimalList(animals) {
  const animalList = Array.isArray(animals) ? animals : [animals];
  return animalList.map(normalizeAnimal).filter(Boolean);
}

export async function fetchAllAnimals() {
  const response = await apiFetch(`${API_BASE_URL}/leggiTutti`);

  if (!response.ok) {
    throw new Error(`Impossibile recuperare gli animali (status ${response.status})`);
  }

  return normalizeAnimalList(await response.json());
}

export async function fetchAnimalsByOwner(ownerId) {
  const response = await apiFetch(`${API_BASE_URL}/leggiPerUtente/${ownerId}`);

  if (!response.ok) {
    throw new Error(`Impossibile recuperare gli animali (status ${response.status})`);
  }

  return normalizeAnimalList(await response.json());
}

export async function fetchAnimalById(animalId) {
  const response = await apiFetch(`${API_BASE_URL}/leggi/${animalId}`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, "Impossibile recuperare i dati dell'animale.");
    throw new Error(errorMessage);
  }

  return normalizeAnimal(await response.json());
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

export async function updateAnimal(animalId, payload) {
  const response = await apiFetchWithPayload(`${API_BASE_URL}/modifica/${animalId}`, [payload], {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, "Impossibile salvare i dati dell'animale.");
    throw new Error(errorMessage);
  }

  return normalizeAnimal(await response.json());
}

export async function sendAnimalEvaluationRequest(payload) {
  const response = await apiFetchWithPayload('/api/richieste-animali/valutazione', [payload]);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile inviare la richiesta di valutazione.');
    throw new Error(errorMessage);
  }

  return response.json();
}

export async function fetchOpenAnimalEvaluationRequests() {
  const response = await apiFetch('/api/richieste-animali/aperte');

  if (!response.ok) {
    throw new Error(`Impossibile recuperare le richieste animali (status ${response.status})`);
  }

  return response.json();
}

export async function closeAnimalEvaluationRequest(requestId) {
  const response = await apiFetch(`/api/richieste-animali/${requestId}/chiudi`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile chiudere la richiesta.');
    throw new Error(errorMessage);
  }
}
