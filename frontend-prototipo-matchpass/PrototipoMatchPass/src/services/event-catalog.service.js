import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const EVENT_PATH = API_ENDPOINTS.events;
const EVENT_VALIDATION_PATH = API_ENDPOINTS.eventValidation;
const VENUE_PATH = API_ENDPOINTS.venues;
const TEAM_PATH = API_ENDPOINTS.teams;
const segment = encodeURIComponent;

export const EventCatalogService = {
  createEvent(payload) {
    return httpClient.post(EVENT_PATH, payload);
  },

  getEventById(eventId) {
    return httpClient.get(`${EVENT_PATH}/${segment(eventId)}`);
  },

  addSectorToEvent(eventId, payload) {
    return httpClient.post(`${EVENT_PATH}/${segment(eventId)}/add-sector`, payload);
  },

  removeSectorFromEvent(eventId, sectorId) {
    return httpClient.delete(`${EVENT_PATH}/${segment(eventId)}/remove-sector/${segment(sectorId)}`);
  },

  getEventSectors(eventId) {
    return httpClient.get(`${EVENT_PATH}/${segment(eventId)}`).then((event) => event?.sectorsDetails ?? []);
  },

  validateEvent(eventId) {
    return httpClient.get(`${EVENT_VALIDATION_PATH}/${segment(eventId)}/exists`);
  },

  validateEventSector(eventId, sectorId) {
    return httpClient.get(`${EVENT_VALIDATION_PATH}/${segment(eventId)}/sector/${segment(sectorId)}/exists`);
  },

  validateSeatCreation(eventId, sectorId, seatsAmount) {
    return httpClient.get(`${EVENT_VALIDATION_PATH}/${segment(eventId)}/sector/${segment(sectorId)}/seats/${segment(seatsAmount)}`);
  },

  listVenues() {
    return httpClient.get(VENUE_PATH);
  },

  getVenueById(venueId) {
    return httpClient.get(`${VENUE_PATH}/${segment(venueId)}`);
  },

  findVenuesByLocation(city, state) {
    return httpClient.get(`${VENUE_PATH}/filter-by-location`, { query: { city, state } });
  },

  createVenue(payload) {
    return httpClient.post(VENUE_PATH, payload);
  },

  addSectorToVenue(venueId, payload) {
    return httpClient.post(`${VENUE_PATH}/${segment(venueId)}/add-sector`, payload);
  },

  removeSectorFromVenue(venueId, sectorId) {
    return httpClient.delete(`${VENUE_PATH}/${segment(venueId)}/remove-sector/${segment(sectorId)}`);
  },

  createTeam(payload) {
    return httpClient.post(TEAM_PATH, payload);
  },
};
