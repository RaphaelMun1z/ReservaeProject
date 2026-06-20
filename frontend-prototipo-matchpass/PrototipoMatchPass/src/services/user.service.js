import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.users;

export const UserService = {
  getCurrentUser() {
    return httpClient.get(`${BASE_PATH}/me`);
  },

  updateCurrentUser(payload) {
    return httpClient.put(`${BASE_PATH}/me`, payload);
  },

  updatePassword(payload) {
    return httpClient.patch(`${BASE_PATH}/me/password`, payload);
  },
};

