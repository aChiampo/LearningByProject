import { apiFetchWithPayload, readApiError } from './apiClient';
import { normalizeUser } from './authApi';

export async function updateUserProfile(userId, payload) {
  const response = await apiFetchWithPayload(`/api/utente/modifica/${userId}`, [payload], {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile aggiornare il profilo.');
    throw new Error(errorMessage);
  }

  return normalizeUser(await response.json());
}
