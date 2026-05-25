// ===================================================
//  MatchPass — cart.js
//  Shared: cart state, timer, CPF validation
// ===================================================

// ---- CART STATE (via sessionStorage) ----
const CART_KEY  = 'mp_cart';
const TIMER_KEY = 'mp_timer_end';

function getCart() {
  try { return JSON.parse(sessionStorage.getItem(CART_KEY)) || []; }
  catch { return []; }
}

function saveCart(cart) {
  sessionStorage.setItem(CART_KEY, JSON.stringify(cart));
}

function addToCart(item) {
  const cart = getCart();
  if (!cart.find(i => i.code === item.code)) {
    cart.push({ ...item, cpf: '' });
    saveCart(cart);
    if (!sessionStorage.getItem(TIMER_KEY)) startTimer();
  }
  renderCart();
}

function removeFromCart(code) {
  let cart = getCart().filter(i => i.code !== code);
  saveCart(cart);
  if (cart.length === 0) sessionStorage.removeItem(TIMER_KEY);
  renderCart();
  // deselect seat on map if present
  const el = document.querySelector(`circle[data-code="${CSS.escape(code)}"]`);
  if (el) el.classList.remove('seat-selected');
}

function updateCpf(code, cpf) {
  const cart = getCart();
  const item = cart.find(i => i.code === code);
  if (item) { item.cpf = cpf; saveCart(cart); }
}

function isInCart(code) {
  return getCart().some(i => i.code === code);
}

// ---- TIMER ----
let _timerInterval = null;

function startTimer() {
  const DURATION = 10 * 60; // 10 minutes
  if (!sessionStorage.getItem(TIMER_KEY)) {
    sessionStorage.setItem(TIMER_KEY, Date.now() + DURATION * 1000);
  }
  _tickTimer();
}

function _tickTimer() {
  if (_timerInterval) clearInterval(_timerInterval);
  _timerInterval = setInterval(() => {
    const end = parseInt(sessionStorage.getItem(TIMER_KEY));
    const remaining = Math.max(0, Math.round((end - Date.now()) / 1000));
    _renderTimer(remaining);
    if (remaining === 0) {
      clearInterval(_timerInterval);
      sessionStorage.removeItem(TIMER_KEY);
      saveCart([]);
      renderCart();
      _showTimerExpiredModal();
    }
  }, 1000);
}

function _renderTimer(seconds) {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  const display = String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');

  const timerEl = document.getElementById('cart-timer');
  const spanEl  = document.getElementById('timer-value');
  if (spanEl) spanEl.textContent = display;
  if (timerEl) timerEl.classList.toggle('urgent', seconds <= 120);
}

function _showTimerExpiredModal() {
  const overlay = document.getElementById('timer-modal');
  if (overlay) overlay.classList.add('open');
}

// ---- CPF VALIDATION ----
function formatCpf(value) {
  return value.replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
    .substring(0, 14);
}

function validateCpf(cpf) {
  const digits = cpf.replace(/\D/g, '');
  if (digits.length !== 11 || /^(\d)\1+$/.test(digits)) return false;
  let sum = 0;
  for (let i = 0; i < 9; i++) sum += parseInt(digits[i]) * (10 - i);
  let r = (sum * 10) % 11;
  if (r === 10 || r === 11) r = 0;
  if (r !== parseInt(digits[9])) return false;
  sum = 0;
  for (let i = 0; i < 10; i++) sum += parseInt(digits[i]) * (11 - i);
  r = (sum * 10) % 11;
  if (r === 10 || r === 11) r = 0;
  return r === parseInt(digits[10]);
}

// ---- RENDER CART ----
function renderCart() {
  const cart   = getCart();
  const listEl = document.getElementById('cart-items');
  const badgeEl = document.getElementById('cart-badge');
  const totalEl = document.getElementById('cart-total');
  const checkoutBtn = document.getElementById('btn-checkout');

  if (badgeEl) badgeEl.textContent = cart.length;
  if (totalEl) {
    const total = cart.reduce((s, i) => s + i.price, 0);
    totalEl.textContent = 'R$ ' + total.toLocaleString('pt-BR', { minimumFractionDigits: 2 });
  }

  if (!listEl) return;

  if (cart.length === 0) {
    listEl.innerHTML = `
      <div class="cart-empty">
        <div class="cart-empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 3h2l.4 2M7 13h10l4-8H5.4"/>
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
          </svg>
        </div>
        Selecione um assento no mapa para adicioná-lo ao carrinho.
      </div>`;
    if (checkoutBtn) checkoutBtn.disabled = true;
    return;
  }

  listEl.innerHTML = cart.map(item => `
    <div class="cart-item" id="ci-${_safe(item.code)}">
      <div class="cart-item-body">
        <div class="cart-item-sector">${_sectorFromCode(item.code)}</div>
        <div class="cart-item-seat">${_seatFromCode(item.code)}</div>
        <div class="cart-item-cpf">
          <div class="cart-item-cpf-label">CPF do portador</div>
          <input
            type="text"
            inputmode="numeric"
            placeholder="000.000.000-00"
            maxlength="14"
            value="${item.cpf || ''}"
            data-code="${item.code}"
            oninput="onCpfInput(this)"
            class="${item.cpf && !validateCpf(item.cpf) ? 'error' : ''}"
          >
        </div>
      </div>
      <div style="display:flex;flex-direction:column;align-items:flex-end;gap:8px;flex-shrink:0">
        <span class="cart-item-price">R$ ${item.price.toLocaleString('pt-BR', {minimumFractionDigits:2})}</span>
        <button class="cart-remove" onclick="removeFromCart('${item.code.replace(/'/g,"\\'")}')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
          </svg>
        </button>
      </div>
    </div>
  `).join('');

  // Checkout button: enable only if all CPFs are filled and valid
  _updateCheckoutBtn();

  // Timer
  const end = parseInt(sessionStorage.getItem(TIMER_KEY));
  if (end) {
    const remaining = Math.max(0, Math.round((end - Date.now()) / 1000));
    _renderTimer(remaining);
    if (!_timerInterval || _timerInterval._dead) startTimer();
    else _tickTimer();
  }
}

function onCpfInput(input) {
  input.value = formatCpf(input.value);
  const code = input.dataset.code;
  updateCpf(code, input.value);
  input.classList.toggle('error', input.value.length > 0 && !validateCpf(input.value));
  _updateCheckoutBtn();
}

function _updateCheckoutBtn() {
  const cart = getCart();
  const btn  = document.getElementById('btn-checkout');
  if (!btn) return;
  const allValid = cart.length > 0 && cart.every(i => validateCpf(i.cpf || ''));
  btn.disabled = !allValid;
  btn.title = allValid ? '' : 'Preencha o CPF válido de cada portador para continuar';
}

// ---- HELPERS ----
function _safe(str) { return str.replace(/[^a-z0-9]/gi, '-'); }
function _sectorFromCode(code) {
  const m = code.match(/^([^-]+(?:-[^F][^i]?[^l]?)?)/);
  const parts = code.split(' - ');
  return parts[0] || code;
}
function _seatFromCode(code) {
  const parts = code.split(' - ');
  if (parts.length >= 3) return `Fileira ${parts[1].replace('Fileira ','')} · ${parts[2]}`;
  return code;
}

function showToast(msg) {
  let t = document.getElementById('toast');
  if (!t) {
    t = document.createElement('div');
    t.id = 'toast';
    t.className = 'toast';
    document.body.appendChild(t);
  }
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 2400);
}

// ---- INIT ----
document.addEventListener('DOMContentLoaded', () => {
  renderCart();
  const end = sessionStorage.getItem(TIMER_KEY);
  if (end && getCart().length > 0) startTimer();
});
