const TOKEN_KEY = 'token';
const USER_KEY = 'username';
const ROLE_KEY = 'role';

export const session = {
  get token() {
    return localStorage.getItem(TOKEN_KEY);
  },
  get username() {
    return localStorage.getItem(USER_KEY);
  },
  get isAdmin() {
    return localStorage.getItem(ROLE_KEY) === 'ADMIN';
  },
  save({ token, username, role }) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, username);
    localStorage.setItem(ROLE_KEY, role);
  },
  clear() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(ROLE_KEY);
  },
};

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

/**
 * Small fetch wrapper: adds the JWT, parses JSON errors ({"error": "..."}) and
 * sends the user back to the login page when the token is no longer valid.
 */
export async function api(path, { method = 'GET', body, authenticated = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (authenticated && session.token) {
    headers.Authorization = `Bearer ${session.token}`;
  }

  const res = await fetch(`/api${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (res.status === 401 && authenticated) {
    session.clear();
    window.location.assign('/login');
    throw new ApiError(401, 'Session expired');
  }
  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new ApiError(res.status, data.error || res.statusText || 'Request failed');
  }
  return res.status === 204 ? null : res.json();
}
