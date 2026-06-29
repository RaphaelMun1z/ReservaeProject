import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.orders;
const segment = encodeURIComponent;

export const OrderService = {
  checkout(payload) {
    return httpClient.post(`${BASE_PATH}/checkout`, payload);
  },

  createOrder(payload) {
    return httpClient.post(`${BASE_PATH}/checkout`, payload);
  },

  getOrderById(orderId) {
    return httpClient.get(`${BASE_PATH}/${segment(orderId)}`);
  },

  updateStatus(orderId, status) {
    return httpClient.patch(`${BASE_PATH}/${segment(orderId)}/status`, status);
  },

  updateProcessStatus(orderId, status) {
    return httpClient.patch(`${BASE_PATH}/${segment(orderId)}/status`, status);
  },

  getOrdersByUserId(userId) {
    return httpClient.get(`${BASE_PATH}/user/${segment(userId)}`);
  },

  getOrderBySeatTag(seatTag) {
    return httpClient.get(`${BASE_PATH}/seat/${segment(seatTag)}`);
  },

  getOrdersByEventId(eventId) {
    return httpClient.get(`${BASE_PATH}/event/${segment(eventId)}`);
  },
};
