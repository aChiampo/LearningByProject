import { apiFetch, apiFetchWithPayload, readApiError } from './apiClient';
import { normalizeUser } from './authApi';

const USERS_API_BASE = '/api/utenti';

function getUserRole(user) {
  return user?.backendRole ?? user?.ruolo?.ruolo ?? user?.role?.ruolo ?? user?.ruolo ?? user?.role;
}

function normalizeUserList(users) {
  const userList = Array.isArray(users) ? users : [users];
  return userList.map(normalizeUser).filter(Boolean);
}

export async function fetchUsers() {
  const response = await apiFetch(`${USERS_API_BASE}/ottieniTutti`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare gli utenti.');
    throw new Error(errorMessage || 'Impossibile recuperare gli utenti.');
  }

  return normalizeUserList(await response.json());
}

export async function fetchClients() {
  const users = await fetchUsers();
  return users.filter((user) => user.role === 'client' || getUserRole(user) === 'CLIENTE');
}

export async function fetchDoctors() {
  const response = await apiFetch(`${USERS_API_BASE}/ottieniVeterinari`);

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Impossibile recuperare i veterinari.');
    throw new Error(errorMessage || 'Impossibile recuperare i veterinari.');
  }

  return normalizeUserList(await response.json())
    .filter((user) => user.role === 'doctor' || getUserRole(user) === 'VETERINARIO');
}

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
