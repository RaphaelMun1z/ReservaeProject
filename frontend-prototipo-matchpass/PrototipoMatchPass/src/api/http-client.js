import { API_CONFIG } from "../constants/api-config.js";

export class ApiError extends Error {
  constructor(message, { status, payload } = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }
}

function buildUrl(path, queryParams = {}) {
  const url = new URL(path, API_CONFIG.baseUrl);

  Object.entries(queryParams)
    .filter(([, value]) => value !== undefined && value !== null && value !== "")
    .forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((item) => url.searchParams.append(key, item));
        return;
      }

      url.searchParams.set(key, value);
    });

  return url.toString();
}

function getAuthToken() {
  return localStorage.getItem("matchpass.accessToken");
}

async function parseResponse(response) {
  const contentType = response.headers.get("content-type") || "";

  if (response.status === 204) {
    return null;
  }

  if (contentType.includes("application/json")) {
    return response.json();
  }

  return response.text();
}

async function request(path, { method = "GET", query, body, headers = {} } = {}) {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), API_CONFIG.timeoutMs);
  const token = getAuthToken();

  try {
    const response = await fetch(buildUrl(path, query), {
      method,
      headers: {
        Accept: "application/json",
        ...(body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...headers,
      },
      body: body ? JSON.stringify(body) : undefined,
      signal: controller.signal,
    });

    const payload = await parseResponse(response);

    if (!response.ok) {
      throw new ApiError("Request failed", {
        status: response.status,
        payload,
      });
    }

    return payload;
  } catch (error) {
    if (error.name === "AbortError") {
      throw new ApiError("Request timed out");
    }

    throw error;
  } finally {
    window.clearTimeout(timeout);
  }
}

export const httpClient = {
  get: (path, options) => request(path, { ...options, method: "GET" }),
  post: (path, body, options) => request(path, { ...options, method: "POST", body }),
  put: (path, body, options) => request(path, { ...options, method: "PUT", body }),
  patch: (path, body, options) => request(path, { ...options, method: "PATCH", body }),
  delete: (path, options) => request(path, { ...options, method: "DELETE" }),
};

