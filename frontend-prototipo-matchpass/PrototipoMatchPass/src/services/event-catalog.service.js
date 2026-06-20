import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.eventCatalog;

export const EventCatalogService = {
  listEvents(filters = {}) {
    return httpClient.get(BASE_PATH, { query: filters });
  },

  getEventById(eventId) {
    return httpClient.get(`${BASE_PATH}/${eventId}`);
  },

  getFeaturedEvents(limit = 6) {
    return httpClient.get(`${BASE_PATH}/featured`, { query: { limit } });
  },

  searchEvents(query, filters = {}) {
    return httpClient.get(`${BASE_PATH}/search`, {
      query: { q: query, ...filters },
    });
  },

  getEventSectors(eventId) {
    return httpClient.get(`${BASE_PATH}/${eventId}/sectors`);
  },

  getEventTickets(eventId) {
    return httpClient.get(`${BASE_PATH}/${eventId}/tickets`);
  },
};

