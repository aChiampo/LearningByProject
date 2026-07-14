// services/animalApi.js
const API_BASE_URL = 'http://localhost:9020/api/animali';

export async function fetchAnimalsByOwner(ownerId) {
  const response = await fetch(`${API_BASE_URL}/leggiPerUtente/${ownerId}`);

  if (!response.ok) {
    throw new Error(`Impossibile recuperare gli animali (status ${response.status})`);
  }

  return response.json();
}