import { httpClient } from "../api/http-client.js";
import { API_ENDPOINTS } from "../constants/api-config.js";

const BASE_PATH = API_ENDPOINTS.tickets;

export const TicketService = {
	listMyTickets(filters = {}) {
		return httpClient.get(`${BASE_PATH}/me`, { query: filters });
	},

	getTicketById(ticketId) {
		return httpClient.get(`${BASE_PATH}/${ticketId}`);
	},

	transferTicket(ticketId, payload) {
		return httpClient.post(`${BASE_PATH}/${ticketId}/transfer`, payload);
	},

	validateTicket(payload) {
		return httpClient.post(`${BASE_PATH}/validate`, payload);
	},
};
