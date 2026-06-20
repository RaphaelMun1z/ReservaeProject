export const API_CONFIG = {
  baseUrl: window.MATCHPASS_API_BASE_URL || "http://localhost:8765",
  timeoutMs: 15000,
};

export const API_ENDPOINTS = {
  eventCatalog: "/event-catalog-service/api/event",
  auth: "/auth-service/api/auth",
  users: "/user-service/api/users",
  tickets: "/ticket-service/api/tickets",
  orders: "/order-service/api/orders",
  payments: "/payment-service/api/payments",
  admin: "/admin-service/api/admin",
};

