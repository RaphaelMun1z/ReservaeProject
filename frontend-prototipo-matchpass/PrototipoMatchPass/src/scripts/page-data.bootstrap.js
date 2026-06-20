const currencyFormatter = new Intl.NumberFormat("pt-BR", {
	style: "currency",
	currency: "BRL",
});

const dateFormatter = new Intl.DateTimeFormat("pt-BR", {
	day: "2-digit",
	month: "short",
	year: "numeric",
	hour: "2-digit",
	minute: "2-digit",
});

const API_CONFIG = {
	baseUrl: window.MATCHPASS_API_BASE_URL || "http://localhost:8765",
	timeoutMs: 15000,
};

const API_ENDPOINTS = {
	eventCatalog: "/event-catalog-service/api/event",
	auth: "/auth-service/api/auth",
	users: "/user-service/api/users",
	tickets: "/ticket-service/api/tickets",
	orders: "/order-service/api/orders",
	admin: "/admin-service/api/admin",
};

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

function getAccessToken() {
	return localStorage.getItem("matchpass.accessToken");
}

async function parseResponse(response) {
	const contentType = response.headers.get("content-type") || "";

	if (response.status === 204) return null;
	if (contentType.includes("application/json")) return response.json();

	return response.text();
}

async function apiRequest(path, { method = "GET", query, body, headers = {} } = {}) {
	const controller = new AbortController();
	const timeout = window.setTimeout(() => controller.abort(), API_CONFIG.timeoutMs);
	const token = getAccessToken();

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
			const error = new Error("Request failed");
			error.status = response.status;
			error.payload = payload;
			throw error;
		}

		return payload;
	} catch (error) {
		if (error.name === "AbortError") {
			const timeoutError = new Error("Request timed out");
			timeoutError.status = 408;
			throw timeoutError;
		}

		throw error;
	} finally {
		window.clearTimeout(timeout);
	}
}

const EventCatalogService = {
	listEvents: (filters = {}) => apiRequest(API_ENDPOINTS.eventCatalog, { query: filters }),
	getEventById: (eventId) => apiRequest(`${API_ENDPOINTS.eventCatalog}/${eventId}`),
	getFeaturedEvents: (limit = 6) => apiRequest(`${API_ENDPOINTS.eventCatalog}/featured`, { query: { limit } }),
	getEventSectors: (eventId) => apiRequest(`${API_ENDPOINTS.eventCatalog}/${eventId}/sectors`),
};

const TicketService = {
	listMyTickets: (filters = {}) => apiRequest(`${API_ENDPOINTS.tickets}/me`, { query: filters }),
	getTicketById: (ticketId) => apiRequest(`${API_ENDPOINTS.tickets}/${ticketId}`),
};

const OrderService = {
	getOrderById: (orderId) => apiRequest(`${API_ENDPOINTS.orders}/${orderId}`),
};

const UserService = {
	getCurrentUser: () => apiRequest(`${API_ENDPOINTS.users}/me`),
};

const AdminService = {
	getDashboardSummary: () => apiRequest(`${API_ENDPOINTS.admin}/dashboard`),
	listEvents: (filters = {}) => apiRequest(`${API_ENDPOINTS.admin}/events`, { query: filters }),
};

const routeLoaders = {
	"event-list": () => EventCatalogService.listEvents(readQueryParams()),
	"featured-events": () => EventCatalogService.getFeaturedEvents(),
	"event-detail": () => {
		const eventId = readPageParam("eventId");
		return eventId
			? EventCatalogService.getEventById(eventId)
			: EventCatalogService.listEvents();
	},
	"event-sectors": () => {
		const eventId = readPageParam("eventId");
		return eventId
			? EventCatalogService.getEventSectors(eventId)
			: EventCatalogService.listEvents();
	},
	checkout: () => Promise.resolve(readCheckoutDraft()),
	"order-detail": () => {
		const orderId = readPageParam("orderId");
		return orderId
			? OrderService.getOrderById(orderId)
			: Promise.resolve(null);
	},
	"user-tickets": () => TicketService.listMyTickets(readQueryParams()),
	"ticket-detail": () => {
		const ticketId = readPageParam("ticketId");
		return ticketId
			? TicketService.getTicketById(ticketId)
			: TicketService.listMyTickets();
	},
	"user-profile": () => UserService.getCurrentUser(),
	"admin-dashboard": () => AdminService.getDashboardSummary(),
	"admin-events": () => AdminService.listEvents(readQueryParams()),
	auth: () =>
		Promise.resolve({
			authenticated: Boolean(getAccessToken()),
		}),
};

function readQueryParams() {
	return Object.fromEntries(new URLSearchParams(window.location.search));
}

function readPageParam(name) {
	const params = new URLSearchParams(window.location.search);
	return document.body.dataset[name] || params.get(name) || params.get("id");
}

function readCheckoutDraft() {
	const rawDraft = sessionStorage.getItem("matchpass.checkoutDraft");
	return rawDraft ? JSON.parse(rawDraft) : null;
}

function emit(name, detail) {
	document.dispatchEvent(new CustomEvent(name, { detail }));
}

function escapeHtml(value = "") {
	return String(value)
		.replaceAll("&", "&amp;")
		.replaceAll("<", "&lt;")
		.replaceAll(">", "&gt;")
		.replaceAll('"', "&quot;")
		.replaceAll("'", "&#039;");
}

function listFromPayload(payload) {
	if (Array.isArray(payload)) return payload;
	if (Array.isArray(payload?.content)) return payload.content;
	if (Array.isArray(payload?.items)) return payload.items;
	if (Array.isArray(payload?.data)) return payload.data;
	if (payload) return [payload];
	return [];
}

function money(value) {
	const amount = value?.amount ?? value?.value ?? value ?? 0;
	return currencyFormatter.format(Number(amount) || 0);
}

function dateTime(value) {
	if (!value) return "";
	const date = new Date(value);
	return Number.isNaN(date.getTime()) ? value : dateFormatter.format(date);
}

function eventLocation(event) {
	const venue = event.venue || {};
	const address = venue.address || {};
	return [
		venue.name,
		address.city,
		address.state,
	].filter(Boolean).join(" - ");
}

function emptyState(message) {
	return `<p class="api-empty-state">${escapeHtml(message)}</p>`;
}

function appRootPrefix() {
	return window.location.pathname.includes("/pages/") ? "../../" : "";
}

function appRoute(pathFromRoot) {
	return `${appRootPrefix()}${pathFromRoot}`;
}

function repeatTemplate(count, template) {
	return Array.from({ length: count }, (_, index) => template(index)).join("");
}

function showEventSkeleton(count = 3) {
	const container =
		document.querySelector(".shows-grid") ||
		document.querySelector(".tickets-grid") ||
		document.querySelector(".tickets");
	if (!container) return;

	const isTicketGrid = container.classList.contains("tickets-grid") || container.classList.contains("tickets");
	container.innerHTML = repeatTemplate(count, () => {
		if (isTicketGrid) {
			return `
				<div class="ticket-card" aria-hidden="true">
					<div class="tc-content">
						<div class="tc-header">
							<span class="skeleton-pill"></span>
							<span class="skeleton-line short"></span>
						</div>
						<span class="skeleton-line tall medium"></span>
						<span class="skeleton-line medium"></span>
						<span class="skeleton-line short"></span>
					</div>
					<div class="tc-divider"></div>
					<div class="tc-action">
						<span class="skeleton-pill"></span>
					</div>
				</div>
			`;
		}

		return `
			<article class="show-card" aria-hidden="true">
				<div class="sc-banner"><div class="skeleton-block skeleton-image"></div></div>
				<div class="sc-content">
					<span class="skeleton-line short"></span>
					<span class="skeleton-line tall medium"></span>
					<span class="skeleton-line"></span>
					<div class="sc-footer">
						<div><span class="skeleton-line short"></span><span class="skeleton-line medium"></span></div>
						<span class="skeleton-pill"></span>
					</div>
				</div>
			</article>
		`;
	});
}

function showSectorSkeleton() {
	const map = document.querySelector(".arena-map");
	const list = document.querySelector(".ticket-list");
	if (map) {
		map.innerHTML = `
			<div class="map-stage">PALCO</div>
			<div class="map-sector zone-vip"><span class="skeleton-line medium"></span></div>
			<div class="map-sector zone-premium"><span class="skeleton-line medium"></span></div>
			<div class="map-sector zone-pista"><span class="skeleton-line medium"></span></div>
		`;
	}

	if (list) {
		list.innerHTML = repeatTemplate(4, () => `
			<div class="ticket-item" aria-hidden="true">
				<div class="ti-info">
					<span class="skeleton-line tall medium"></span>
					<span class="skeleton-line short"></span>
				</div>
				<div class="qty-control skeleton-actions">
					<span class="skeleton-pill"></span>
				</div>
			</div>
		`);
	}
}

function showTicketListSkeleton() {
	const container = document.querySelector(".ticket-list");
	if (!container) return;

	container.innerHTML = repeatTemplate(2, () => `
		<article class="e-ticket" aria-hidden="true">
			<div class="et-image"><div class="skeleton-block skeleton-image"></div></div>
			<div class="et-content">
				<div class="et-header"><span class="skeleton-pill"></span></div>
				<span class="skeleton-line tall medium"></span>
				<span class="skeleton-line medium"></span>
				<div class="et-details">
					${repeatTemplate(4, () => `<div class="detail-group"><span class="skeleton-line short"></span><span class="skeleton-line medium"></span></div>`)}
				</div>
			</div>
			<div class="et-qr-section">
				<div class="qr-code"><div class="skeleton-block skeleton-qr"></div></div>
			</div>
		</article>
	`);
}

function showTicketDetailSkeleton() {
	const container = document.querySelector(".api-ticket-detail");
	if (!container) return;

	container.innerHTML = `
		<div class="super-ticket" aria-hidden="true">
			<div class="st-banner"><div class="skeleton-block skeleton-image"></div></div>
			<div class="st-body">
				<span class="skeleton-line short"></span>
				<span class="skeleton-line tall medium"></span>
				<div class="st-info-grid">
					${repeatTemplate(4, () => `<div class="st-info-item"><span class="skeleton-line short"></span><span class="skeleton-line medium"></span></div>`)}
				</div>
			</div>
			<div class="st-divider"></div>
			<div class="st-qr-area"><div class="skeleton-block skeleton-qr"></div></div>
		</div>
	`;
}

function showOrderSkeleton() {
	const container = document.querySelector(".api-order-summary");
	if (!container) return;

	container.innerHTML = `
		<div class="totals-list" aria-hidden="true">
			${repeatTemplate(3, () => `<div class="total-row"><span class="skeleton-line medium"></span><span class="skeleton-line short"></span></div>`)}
			<div class="total-row grand-total"><span class="skeleton-line medium"></span><span class="skeleton-line short"></span></div>
		</div>
	`;
}

function showProfileSkeleton() {
	const nameElement = document.querySelector("[data-user-name]");
	const emailElement = document.querySelector("[data-user-email]");
	if (nameElement) nameElement.innerHTML = '<span class="skeleton-line medium"></span>';
	if (emailElement) emailElement.innerHTML = '<span class="skeleton-line"></span>';

	document.querySelectorAll('input[type="text"], input[type="email"], input[type="tel"]').forEach((input) => {
		input.placeholder = "Carregando...";
	});
}

function showAdminSkeleton() {
	document.querySelectorAll(".text-3xl.font-black.text-white, .text-2xl.font-black.text-white").forEach((element) => {
		if (!element.textContent.trim()) {
			element.innerHTML = '<span class="skeleton-line medium tall"></span>';
		}
	});

	document.querySelectorAll("tbody.divide-y").forEach((tbody) => {
		if (!tbody.children.length) {
			tbody.innerHTML = repeatTemplate(4, () => `
				<tr aria-hidden="true">
					<td class="py-4 px-6" colspan="6"><div class="skeleton-card-row"><span class="skeleton-avatar"></span><div><span class="skeleton-line medium"></span><span class="skeleton-line short"></span></div></div></td>
				</tr>
			`);
		}
	});
}

function renderSkeleton(pageKey) {
	if (pageKey === "event-list" || pageKey === "featured-events") showEventSkeleton();
	if (pageKey === "event-sectors") showSectorSkeleton();
	if (pageKey === "user-tickets") showTicketListSkeleton();
	if (pageKey === "ticket-detail") showTicketDetailSkeleton();
	if (pageKey === "checkout" || pageKey === "order-detail") showOrderSkeleton();
	if (pageKey === "user-profile") showProfileSkeleton();
	if (pageKey === "admin-dashboard" || pageKey === "admin-events") showAdminSkeleton();
}

function getErrorTarget(pageKey) {
	if (pageKey === "event-list" || pageKey === "featured-events") {
		return document.querySelector(".shows-grid") || document.querySelector(".tickets-grid") || document.querySelector(".tickets");
	}

	if (pageKey === "event-sectors") {
		return document.querySelector(".ticket-list") || document.querySelector(".arena-map");
	}

	if (pageKey === "user-tickets") {
		return document.querySelector(".ticket-list");
	}

	if (pageKey === "ticket-detail") {
		return document.querySelector(".api-ticket-detail") || document.querySelector(".ticket-list");
	}

	if (pageKey === "checkout" || pageKey === "order-detail") {
		return document.querySelector(".api-order-summary");
	}

	if (pageKey === "user-profile") {
		return document.querySelector(".content-area") || document.querySelector(".profile-container");
	}

	if (pageKey === "admin-dashboard" || pageKey === "admin-events") {
		return document.querySelector(".page-view:not(.hidden)") || document.querySelector("main");
	}

	return document.querySelector("main");
}

function renderErrorState(pageKey, error) {
	const target = getErrorTarget(pageKey);
	if (!target) return;

	const message =
		error?.status === 404
			? "Nao encontramos os dados solicitados. Verifique o identificador ou volte para a lista."
			: "Nao foi possivel carregar os dados agora. Confira se a API esta online e tente novamente.";

	target.innerHTML = `
		<div class="api-error-state" role="alert">
			<p class="api-error-title">Nao foi possivel carregar o conteudo</p>
			<p class="api-error-message">${message}</p>
			<button class="api-retry-button" type="button" data-api-retry>Tentar novamente</button>
		</div>
	`;

	target.querySelector("[data-api-retry]")?.addEventListener("click", () => {
		bootstrapPageData();
	});
}

function renderEventList(data) {
	const container =
		document.querySelector(".shows-grid") ||
		document.querySelector(".tickets-grid") ||
		document.querySelector(".tickets");
	if (!container) return;

	const events = listFromPayload(data);
	if (!events.length) {
		container.innerHTML = emptyState("Nenhum evento encontrado.");
		return;
	}

	const featuredEvent = events[0];
	const locationElement = document.querySelector("[data-event-location]");
	const dateElement = document.querySelector("[data-event-date]");
	if (locationElement) locationElement.textContent = eventLocation(featuredEvent);
	if (dateElement) dateElement.textContent = dateTime(featuredEvent.startsAt || featuredEvent.date);

	container.innerHTML = events.map((event) => {
		const isTicketGrid = container.classList.contains("tickets-grid") || container.classList.contains("tickets");
		if (isTicketGrid) {
			return `
				<div class="ticket-card">
					<div class="tc-content">
						<div class="tc-header">
							<span class="tc-badge">${escapeHtml(event.status || "EVENTO")}</span>
							<span class="tc-date">${escapeHtml(dateTime(event.startsAt || event.date))}</span>
						</div>
						<h3 class="tc-title">${escapeHtml(event.name || event.title || "Evento sem nome")}</h3>
						<span class="tc-subtitle">${escapeHtml(eventLocation(event))}</span>
						<div class="tc-price">${money(event.minPrice || event.price)} <span>+ taxas</span></div>
					</div>
					<div class="tc-divider"></div>
					<div class="tc-action">
						<a class="tc-btn" href="${appRoute(`pages/checkout/sector-selection.html?eventId=${encodeURIComponent(event.id || "")}`)}">COMPRAR AGORA</a>
					</div>
				</div>
			`;
		}

		return `
		<article class="show-card">
			<div class="sc-banner">
				${event.status ? `<span class="sc-badge">${escapeHtml(event.status)}</span>` : ""}
				${event.imageUrl || event.bannerUrl ? `<img src="${escapeHtml(event.imageUrl || event.bannerUrl)}" alt="${escapeHtml(event.name || event.title || "Evento")}">` : ""}
			</div>
			<div class="sc-content">
				<div class="sc-date">${escapeHtml(dateTime(event.startsAt || event.date))}</div>
				<h3 class="sc-title">${escapeHtml(event.name || event.title || "Evento sem nome")}</h3>
				<div class="sc-location">${escapeHtml(eventLocation(event))}</div>
				<div class="sc-footer">
					<div class="sc-price">A partir de<br>${money(event.minPrice || event.price)} <span>+ taxas</span></div>
					<a class="sc-btn" href="${appRoute(`pages/checkout/sector-selection.html?eventId=${encodeURIComponent(event.id || "")}`)}">VER INGRESSOS</a>
				</div>
			</div>
		</article>
	`;
	}).join("");
}

function renderEventSectors(data) {
	const sectors = listFromPayload(data);
	const map = document.querySelector(".arena-map");
	const list = document.querySelector(".ticket-list");
	const subtitle = document.querySelector(".page-subtitle");

	if (subtitle && data?.name) {
		subtitle.textContent = `${data.name} - ${eventLocation(data)}`;
	}

	if (!map || !list) return;

	if (!sectors.length) {
		map.innerHTML = '<div class="map-stage">PALCO</div>';
		list.innerHTML = emptyState("Nenhum setor disponivel para este evento.");
		return;
	}

	map.innerHTML = '<div class="map-stage">PALCO</div>' + sectors.map((sector) => `
		<div class="map-sector zone-pista" data-sector="${escapeHtml(sector.id)}">
			${escapeHtml(sector.name)}
		</div>
	`).join("");

	list.innerHTML = sectors.map((sector) => `
		<div class="ticket-item" id="item-${escapeHtml(sector.id)}">
			<div class="ti-info">
				<h3 class="ti-title">${escapeHtml(sector.name)}</h3>
				<div class="ti-price">${escapeHtml(sector.lotName || "Lote atual")} - <strong>${money(sector.price)}</strong></div>
			</div>
			<div class="qty-control">
				<button class="qty-btn btn-minus" data-id="${escapeHtml(sector.id)}" disabled>-</button>
				<span class="qty-value" id="qty-${escapeHtml(sector.id)}">0</span>
				<button class="qty-btn btn-plus" data-id="${escapeHtml(sector.id)}" data-price="${Number(sector.price?.amount ?? sector.price ?? 0)}">+</button>
			</div>
		</div>
	`).join("");

	initializeSectorCart();
}

function initializeSectorCart() {
	const checkoutBar = document.getElementById("checkout-bar");
	const totalQty = document.getElementById("total-qty");
	const totalPrice = document.getElementById("total-price");
	const cart = {};

	function updateCart() {
		const entries = Object.values(cart);
		const quantity = entries.reduce((total, item) => total + item.quantity, 0);
		const price = entries.reduce((total, item) => total + item.quantity * item.price, 0);

		if (totalQty) totalQty.textContent = quantity;
		if (totalPrice) totalPrice.textContent = new Intl.NumberFormat("pt-BR").format(price);
		if (checkoutBar) checkoutBar.classList.toggle("visible", quantity > 0);

		sessionStorage.setItem("matchpass.checkoutDraft", JSON.stringify({ items: cart }));
	}

	document.querySelectorAll(".btn-plus").forEach((button) => {
		button.addEventListener("click", () => {
			const id = button.dataset.id;
			const price = Number(button.dataset.price || 0);
			cart[id] = cart[id] || { sectorId: id, quantity: 0, price };
			cart[id].quantity += 1;
			document.getElementById(`qty-${id}`).textContent = cart[id].quantity;
			button.parentElement.querySelector(".btn-minus").disabled = false;
			document.getElementById(`item-${id}`)?.classList.add("active");
			updateCart();
		});
	});

	document.querySelectorAll(".btn-minus").forEach((button) => {
		button.addEventListener("click", () => {
			const id = button.dataset.id;
			if (!cart[id]?.quantity) return;
			cart[id].quantity -= 1;
			document.getElementById(`qty-${id}`).textContent = cart[id].quantity;
			button.disabled = cart[id].quantity === 0;
			document.getElementById(`item-${id}`)?.classList.toggle("active", cart[id].quantity > 0);
			updateCart();
		});
	});
}

function renderUserTickets(data) {
	const container = document.querySelector(".ticket-list");
	if (!container) return;

	const tickets = listFromPayload(data);
	if (!tickets.length) {
		container.innerHTML = emptyState("Nenhum ingresso encontrado.");
		return;
	}

	container.innerHTML = tickets.map((ticket) => {
		const event = ticket.event || {};
		const sector = ticket.sector || {};
		return `
			<article class="e-ticket">
				<div class="et-image">
					${event.imageUrl || event.bannerUrl ? `<img src="${escapeHtml(event.imageUrl || event.bannerUrl)}" alt="${escapeHtml(event.name || "Evento")}">` : ""}
				</div>
				<div class="et-content">
					<div class="et-header"><span class="status-badge">${escapeHtml(ticket.status || "CONFIRMADO")}</span></div>
					<h2 class="et-title">${escapeHtml(event.name || "Evento")}</h2>
					<div class="et-subtitle">${escapeHtml(event.artistName || event.tourName || "")}</div>
					<div class="et-details">
						<div class="detail-group"><span class="detail-label">Data e Hora</span><span class="detail-value">${escapeHtml(dateTime(event.startsAt))}</span></div>
						<div class="detail-group"><span class="detail-label">Local</span><span class="detail-value">${escapeHtml(eventLocation(event))}</span></div>
						<div class="detail-group"><span class="detail-label">Setor</span><span class="detail-value">${escapeHtml(sector.name || "")}</span></div>
						<div class="detail-group"><span class="detail-label">Titular</span><span class="detail-value">${escapeHtml(ticket.holderName || "")}</span></div>
					</div>
				</div>
				<div class="et-qr-section">
					<div class="qr-code">${ticket.qrCode ? `<img src="${escapeHtml(ticket.qrCode)}" alt="QR Code">` : ""}</div>
					<div class="qr-info-mobile">
						<div class="ticket-id">${escapeHtml(ticket.code || ticket.id || "")}</div>
						<a class="btn-download" href="ticket-details.html?ticketId=${encodeURIComponent(ticket.id || "")}">Detalhes</a>
						<a class="btn-download" href="ticket-transfer.html?ticketId=${encodeURIComponent(ticket.id || "")}">Transferir</a>
					</div>
				</div>
			</article>
		`;
	}).join("");
}

function renderTicketDetail(data) {
	const container = document.querySelector(".api-ticket-detail");
	if (!container) {
		renderUserTickets(data);
		return;
	}

	const ticket = listFromPayload(data)[0];
	if (!ticket) {
		container.innerHTML = emptyState("Ingresso nao encontrado.");
		return;
	}

	const event = ticket.event || {};
	const sector = ticket.sector || {};
	container.innerHTML = `
		<div class="super-ticket">
			<div class="st-banner">
				${ticket.status ? `<span class="status-badge">${escapeHtml(ticket.status)}</span>` : ""}
				${event.imageUrl || event.bannerUrl ? `<img src="${escapeHtml(event.imageUrl || event.bannerUrl)}" alt="${escapeHtml(event.name || "Evento")}">` : ""}
			</div>
			<div class="st-body">
				<div class="st-subtitle">${escapeHtml(event.tourName || event.artistName || "")}</div>
				<h1 class="st-title">${escapeHtml(event.name || "Evento")}</h1>
				<div class="st-info-grid">
					<div class="st-info-item"><span class="st-label">Data e Hora</span><span class="st-value">${escapeHtml(dateTime(event.startsAt))}</span></div>
					<div class="st-info-item"><span class="st-label">Titular</span><span class="st-value">${escapeHtml(ticket.holderName || "")}</span></div>
					<div class="st-info-item full"><span class="st-label">Local</span><span class="st-value">${escapeHtml(eventLocation(event))}</span></div>
					<div class="st-info-item full"><span class="st-label">Setor</span><span class="st-value">${escapeHtml(sector.name || "")}</span></div>
				</div>
			</div>
			<div class="st-divider"></div>
			<div class="st-qr-area">
				<div class="qr-wrapper">${ticket.qrCode ? `<img src="${escapeHtml(ticket.qrCode)}" alt="QR Code">` : ""}</div>
				<div class="st-ticket-id">${escapeHtml(ticket.code || ticket.id || "")}</div>
				<a class="btn-download" href="ticket-transfer.html?ticketId=${encodeURIComponent(ticket.id || "")}">Transferir ingresso</a>
			</div>
		</div>
	`;
}

function renderOrderSummary(data) {
	const container = document.querySelector(".api-order-summary");
	if (!container) return;

	const order = Array.isArray(data) ? data[0] : data;
	if (!order) {
		container.innerHTML = emptyState("Nenhum pedido carregado.");
		return;
	}

	const items = listFromPayload(order.items);
	container.innerHTML = `
		<div class="totals-list">
			${items.map((item) => `
				<div class="total-row">
					<span>${escapeHtml(item.name || item.eventName || item.sectorName || "Item")}</span>
					<span>${money(item.total || item.unitPrice || item.price)}</span>
				</div>
			`).join("")}
			${order.fees ? `<div class="total-row"><span>Taxas</span><span>${money(order.fees)}</span></div>` : ""}
			<div class="total-row grand-total">
				<span>Total</span>
				<span>${money(order.total || order.amount)}</span>
			</div>
		</div>
	`;
}

function renderUserProfile(data) {
	const user = Array.isArray(data) ? data[0] : data;
	if (!user) return;

	const nameElement = document.querySelector("[data-user-name]");
	const emailElement = document.querySelector("[data-user-email]");
	const avatarElement = document.querySelector("[data-user-avatar]");
	if (nameElement) nameElement.textContent = user.name || "";
	if (emailElement) emailElement.textContent = user.email || "";
	if (avatarElement && user.avatarUrl) avatarElement.src = user.avatarUrl;

	document.querySelectorAll('input[type="text"], input[type="email"], input[type="tel"]').forEach((input) => {
		const key = input.name || input.id || "";
		if (key.includes("email")) input.value = user.email || "";
		if (key.includes("phone") || key.includes("telefone")) input.value = user.phone || "";
		if (key.includes("name") || key.includes("nome")) input.value = user.name || "";
	});
}

function renderAdminDashboard(data) {
	const summary = Array.isArray(data) ? data[0] : data;
	if (!summary) return;

	const values = [
		summary.totalRevenue ? money(summary.totalRevenue) : null,
		summary.ticketsSold,
		summary.totalCustomers,
		summary.totalOrders,
	].filter((value) => value !== null && value !== undefined);

	document.querySelectorAll(".text-2xl.font-black, .text-3xl.font-black").forEach((element, index) => {
		if (values[index] !== undefined) {
			element.textContent = values[index];
		}
	});
}

function renderApiData(pageKey, data) {
	if (pageKey === "event-list" || pageKey === "featured-events") renderEventList(data);
	if (pageKey === "event-sectors") renderEventSectors(data);
	if (pageKey === "user-tickets") renderUserTickets(data);
	if (pageKey === "ticket-detail") renderTicketDetail(data);
	if (pageKey === "checkout" || pageKey === "order-detail") renderOrderSummary(data);
	if (pageKey === "user-profile") renderUserProfile(data);
	if (pageKey === "admin-dashboard" || pageKey === "admin-events") renderAdminDashboard(data);
}

function getToastContainer() {
	let container = document.querySelector("[data-toast-container]");

	if (!container) {
		container = document.createElement("div");
		container.className = "toast-container";
		container.dataset.toastContainer = "";
		document.body.appendChild(container);
	}

	return container;
}

function removeToast(toast) {
	if (!toast || toast.dataset.removing === "true") {
		return;
	}

	toast.dataset.removing = "true";
	toast.classList.add("is-leaving");
	window.setTimeout(() => toast.remove(), 180);
}

function showErrorToast(error) {
	const container = getToastContainer();
	const toast = document.createElement("div");
	let autoCloseTimeout;
	const message =
		error?.status === 404
			? "Nao encontramos os dados solicitados. Tente atualizar a pagina."
			: "Nao foi possivel carregar os dados da API. Verifique se os servicos estao online.";

	toast.className = "toast";
	toast.setAttribute("role", "alert");
	toast.innerHTML = `
		<div>
			<p class="toast-title">Erro ao carregar conteudo</p>
			<p class="toast-message">${message}</p>
		</div>
		<button class="toast-close" type="button" aria-label="Fechar aviso">&times;</button>
	`;

	toast.querySelector(".toast-close").addEventListener("click", () => {
		window.clearTimeout(autoCloseTimeout);
		removeToast(toast);
	});

	toast.addEventListener("mouseenter", () => {
		window.clearTimeout(autoCloseTimeout);
	});

	toast.addEventListener("mouseleave", () => {
		autoCloseTimeout = window.setTimeout(() => removeToast(toast), 12000);
	});

	container.appendChild(toast);
	autoCloseTimeout = window.setTimeout(() => removeToast(toast), 12000);
}

async function bootstrapPageData() {
	const pageKey = document.body.dataset.apiPage;

	if (!pageKey || !routeLoaders[pageKey]) {
		return;
	}

	window.matchPassData = window.matchPassData || {};
	window.matchPassData.loading = true;
	renderSkeleton(pageKey);
	emit("matchpass:data-loading", { pageKey });

	try {
		const data = await routeLoaders[pageKey]();
		window.matchPassData[pageKey] = data;
		window.matchPassData.loading = false;
		renderApiData(pageKey, data);
		emit("matchpass:data-loaded", { pageKey, data });
	} catch (error) {
		window.matchPassData.loading = false;
		window.matchPassData.lastError = error;
		emit("matchpass:data-error", { pageKey, error });
		renderErrorState(pageKey, error);
		showErrorToast(error);
		console.warn(
			`[MatchPass] Falha ao carregar dados da pagina "${pageKey}".`,
			error,
		);
	}
}

bootstrapPageData();
