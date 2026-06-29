import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.tickets;
const ACCESS_PATH = API_ENDPOINTS.ticketAccess;
const segment = encodeURIComponent;

export const TicketService = {
  generateTickets(payload) {
    return httpClient.post(BASE_PATH, payload);
  },

  getTicketById(ticketId) {
    return httpClient.get(`${BASE_PATH}/${segment(ticketId)}`);
  },

  getTicketsByUser(userId) {
    return httpClient.get(`${BASE_PATH}/user/${segment(userId)}`);
  },

  getTicketsByEvent(eventId, pageable = {}) {
    return httpClient.get(`${BASE_PATH}/event/${segment(eventId)}`, { query: pageable });
  },

  revokeTicket(ticketId) {
    return httpClient.patch(`${BASE_PATH}/${segment(ticketId)}/revoke`);
  },

  validateAccess(payload) {
    return httpClient.post(`${ACCESS_PATH}/validate`, payload);
  },

  validateTicket(payload) {
    return httpClient.post(`${ACCESS_PATH}/validate`, payload);
  },

  getAccessLogs(filters = {}) {
    return httpClient.get(`${ACCESS_PATH}/logs`, { query: filters });
  },
};
