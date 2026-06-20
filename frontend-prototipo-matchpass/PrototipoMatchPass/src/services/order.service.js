import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.orders;

export const OrderService = {
  createOrder(payload) {
    return httpClient.post(BASE_PATH, payload);
  },

  getOrderById(orderId) {
    return httpClient.get(`${BASE_PATH}/${orderId}`);
  },

  confirmOrder(orderId, payload) {
    return httpClient.post(`${BASE_PATH}/${orderId}/confirm`, payload);
  },
};

