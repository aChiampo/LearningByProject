import {
  apiFetch,
  readApiError,
  storeAuthSession,
} from './apiClient';
import { normalizeRole } from '../data/roleConfig';

function normalizeUser(user) {
  if (!user) {
    return null;
  }

  return {
    id: user.id ?? user.userId ?? user.idUtente,
    email: user.email,
    backendRole: user.ruolo ?? user.role,
    role: normalizeRole(user.ruolo ?? user.role),
    nome: user.nome ?? '',
    cognome: user.cognome ?? '',
  };
}

function normalizeLoginResponse(data) {
  const user = normalizeUser(data.utente ?? data.user ?? data);

  return {
    token: data.token,
    tokenType: data.tipoToken ?? data.tokenType ?? 'Bearer',
    expiresIn: data.scadenzaSecondi ?? data.expiresIn,
    user,
  };
}

export async function loginUser(payload) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Credenziali non valide.');
    throw new Error(errorMessage);
  }

  const session = normalizeLoginResponse(await response.json());

  if (!session.token || !session.user?.role) {
    throw new Error('La risposta di autenticazione non contiene tutti i dati necessari.');
  }

  storeAuthSession(session);
  return session;
}

export async function fetchCurrentUser() {
  const response = await apiFetch('/api/auth/me');

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Sessione non valida.');
    throw new Error(errorMessage);
  }

  return normalizeUser(await response.json());
}

export async function registerClient(payload) {
  const response = await fetch('/api/auth/signin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorMessage = await readApiError(response, 'Registrazione non riuscita.');
    throw new Error(errorMessage || 'Registrazione non riuscita.');
  }

  return normalizeLoginResponse(await response.json());
}
