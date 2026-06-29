import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.notifications;

export const NotificationService = {
  sendNotification(payload) {
    return httpClient.post(`${BASE_PATH}/send`, payload);
  },

  send(payload) {
    return httpClient.post(`${BASE_PATH}/send`, payload);
  },
};
