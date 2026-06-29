import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.inventory;
const segment = encodeURIComponent;

export const InventoryService = {
  releaseSeat(seatTag) {
    return httpClient.post(`${BASE_PATH}/release/${segment(seatTag)}`);
  },

  lockSeat(seatTag, userId) {
    return httpClient.post(`${BASE_PATH}/lock-seat/${segment(seatTag)}/user/${segment(userId)}`);
  },

  tryLockSeat(seatTag, userId) {
    return httpClient.post(`${BASE_PATH}/lock-seat/${segment(seatTag)}/user/${segment(userId)}`);
  },

  createSeats(eventId, sectorId, amount) {
    return httpClient.post(`${BASE_PATH}/create-seats/event/${segment(eventId)}/sector/${segment(sectorId)}/amount/${segment(amount)}`);
  },

  confirmSeatSold(seatTag) {
    return httpClient.patch(`${BASE_PATH}/confirm/${segment(seatTag)}`);
  },

  getUserSeats(userId) {
    return httpClient.get(`${BASE_PATH}/user/${segment(userId)}`);
  },

  getSeatById(seatId) {
    return httpClient.get(`${BASE_PATH}/seat/${segment(seatId)}`);
  },

  getEventSectorSeats(eventId, sectorId, status) {
    return httpClient.get(`${BASE_PATH}/event/${segment(eventId)}/sector/${segment(sectorId)}/seats`, { query: { status } });
  },

  getEventSeats(eventId, status) {
    return httpClient.get(`${BASE_PATH}/event/${segment(eventId)}/seat`, { query: { status } });
  },

  checkSeatStatus(seatTag) {
    return httpClient.get(`${BASE_PATH}/check/${segment(seatTag)}`);
  },
};
