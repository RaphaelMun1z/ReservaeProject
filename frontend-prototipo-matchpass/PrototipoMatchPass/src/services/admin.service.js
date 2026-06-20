import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.admin;

export const AdminService = {
  getDashboardSummary() {
    return httpClient.get(`${BASE_PATH}/dashboard`);
  },

  listEvents(filters = {}) {
    return httpClient.get(`${BASE_PATH}/events`, { query: filters });
  },

  createEvent(payload) {
    return httpClient.post(`${BASE_PATH}/events`, payload);
  },

  updateSettings(payload) {
    return httpClient.put(`${BASE_PATH}/settings`, payload);
  },
};

