import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.auth;
const ACCESS_TOKEN_KEY = "matchpass.accessToken";
const REFRESH_TOKEN_KEY = "matchpass.refreshToken";

function persistSession(session) {
  if (session?.accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, session.accessToken);
  }

  if (session?.refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, session.refreshToken);
  }

  return session;
}

export const AuthService = {
  login(credentials) {
    return httpClient.post(`${BASE_PATH}/login`, credentials).then(persistSession);
  },

  register(payload) {
    return httpClient.post(`${BASE_PATH}/register`, payload).then(persistSession);
  },

  requestPasswordReset(payload) {
    return httpClient.post(`${BASE_PATH}/forgot-password`, payload);
  },

  logout() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },
};

