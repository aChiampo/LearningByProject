const AUTH_STORAGE_KEY = 'vetmanager.auth';

export function readStoredAuthSession() {
  try {
    const storedValue = window.localStorage.getItem(AUTH_STORAGE_KEY);
    return storedValue ? JSON.parse(storedValue) : null;
  } catch {
    return null;
  }
}

export function storeAuthSession(session) {
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
}

export function clearAuthSession() {
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
}

export function getAuthToken() {
  return readStoredAuthSession()?.token ?? null;
}

export function getAuthHeaders(headers = {}) {
  const token = getAuthToken();

  if (!token) {
    return headers;
  }

  return {
    ...headers,
    Authorization: `Bearer ${token}`,
  };
}

export async function apiFetch(url, options = {}) {
  return fetch(url, {
    ...options,
    headers: getAuthHeaders(options.headers),
  });
}

export function buildPayload(params = []) {
  const paramList = Array.isArray(params) ? params : [params];

  return paramList.reduce((payload, param) => {
    if (!param || typeof param !== 'object' || Array.isArray(param)) {
      return payload;
    }

    return {
      ...payload,
      ...param,
    };
  }, {});
}

export async function apiFetchWithPayload(endpointPath, params = [], options = {}) {
  const { headers, ...requestOptions } = options;

  return apiFetch(endpointPath, {
    method: 'POST',
    ...requestOptions,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: JSON.stringify(buildPayload(params)),
  });
}

export async function readApiError(response, fallbackMessage) {
  const contentType = response.headers.get('content-type') ?? '';

  if (contentType.includes('application/json')) {
    const errorBody = await response.json();
    return errorBody.message ?? errorBody.error ?? fallbackMessage;
  }

  const errorText = await response.text();
  return errorText || fallbackMessage;
}
