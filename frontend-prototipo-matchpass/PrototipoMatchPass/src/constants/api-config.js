export const API_CONFIG = {
  baseUrl: window.MATCHPASS_API_BASE_URL || "http://localhost:8765",
  timeoutMs: 15000,
};

export const API_ENDPOINTS = {
  events: "/event-catalog-service/api/event/v1",
  eventValidation: "/event-catalog-service/api/event/validate/v1",
  venues: "/event-catalog-service/api/venue/v1",
  teams: "/event-catalog-service/api/team/v1",
  inventory: "/inventory-service/api/inventory",
  auth: "/auth-service/api/auth",
  users: "/user-service/api/users",
  tickets: "/ticket-service/api/ticket/v1",
  ticketAccess: "/ticket-service/api/ticket/access/v1",
  orders: "/order-service/api/order",
  notifications: "/notification-service/api/notifications",
  payments: "/payment-service/api/payments",
  admin: "/admin-service/api/admin",
};
