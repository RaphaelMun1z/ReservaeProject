// ===================================================
//  Mapa de Setores - Santos F.C.
//  app.js
// ===================================================

// --- CARRINHO ---
let shoppingCart = [];

// --- AUXILIARES ---
function getRowLetter(index) {
    let label = '';
    while (index >= 0) {
        label = String.fromCharCode(65 + (index % 26)) + label;
        index = Math.floor(index / 26) - 1;
    }
    return label;
}

function isPointInPoly(pt, polyPoints) {
    let inside = false;
    for (let i = 0, j = polyPoints.length - 1; i < polyPoints.length; j = i++) {
        let xi = polyPoints[i].x, yi = polyPoints[i].y;
        let xj = polyPoints[j].x, yj = polyPoints[j].y;
        let intersect = ((yi > pt.y) !== (yj > pt.y)) &&
                        (pt.x < (xj - xi) * (pt.y - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
    }
    return inside;
}

// --- GERAÇÃO DE ASSENTOS ---
function generateSeats(polygon, sectorName, priceValue) {
    const seatsLayer = document.getElementById('seats-layer');
    seatsLayer.innerHTML = '';

    const pointsList = polygon.points;
    const polyPoints = [];
    for (let i = 0; i < pointsList.numberOfItems; i++) {
        polyPoints.push({ x: pointsList.getItem(i).x, y: pointsList.getItem(i).y });
    }

    const bbox = polygon.getBBox();
    const seatRadius = 3.5;
    const seatSpacing = 15.0;
    let rowIdx = 0;

    for (let y = bbox.y + seatSpacing; y < bbox.y + bbox.height; y += seatSpacing) {
        let colIdx = 1;
        let rowHasSeats = false;

        for (let x = bbox.x + seatSpacing; x < bbox.x + bbox.width; x += seatSpacing) {
            if (isPointInPoly({ x, y }, polyPoints)) {
                rowHasSeats = true;
                const rowLetter = getRowLetter(rowIdx);
                const seatCode = `${sectorName} - Fileira ${rowLetter} - ${colIdx}`;
                const isReserved = Math.random() < 0.35;

                const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
                circle.setAttribute("cx", x);
                circle.setAttribute("cy", y);
                circle.setAttribute("r", seatRadius);
                circle.dataset.code = seatCode;
                circle.dataset.price = priceValue;

                if (isReserved) {
                    circle.setAttribute("class", "seat-reserved");
                } else {
                    const isInCart = shoppingCart.some(item => item.code === seatCode);
                    circle.setAttribute("class", isInCart ? "seat-available seat-selected" : "seat-available");

                    circle.onclick = function (e) {
                        e.stopPropagation();
                        const code = this.dataset.code;
                        const price = parseInt(this.dataset.price);

                        if (this.classList.contains('seat-selected')) {
                            this.classList.remove('seat-selected');
                            shoppingCart = shoppingCart.filter(item => item.code !== code);
                        } else {
                            this.classList.add('seat-selected');
                            shoppingCart.push({ code, price });
                        }
                        updateCartUI();
                    };
                }
                seatsLayer.appendChild(circle);
                colIdx++;
            }
        }
        if (rowHasSeats) rowIdx++;
    }
}

// --- CARRINHO UI ---
function updateCartUI() {
    const container = document.getElementById('cart-items-list');
    const countBadge = document.getElementById('cart-count');
    const totalEl = document.getElementById('cart-total-price');

    container.innerHTML = '';
    let total = 0;

    if (shoppingCart.length === 0) {
        container.innerHTML = '<div class="empty-cart">Seu carrinho está vazio.</div>';
    } else {
        shoppingCart.forEach(item => {
            total += item.price;
            container.innerHTML += `
                <div class="cart-item">
                    <div class="cart-item-info">
                        <span class="cart-item-code">${item.code}</span>
                        <span class="cart-item-price">R$ ${item.price},00</span>
                    </div>
                    <button class="remove-btn" onclick="removeFromCart('${item.code}')">Remover</button>
                </div>
            `;
        });
    }

    countBadge.textContent = shoppingCart.length;
    totalEl.textContent = `R$ ${total},00`;
}

function removeFromCart(code) {
    shoppingCart = shoppingCart.filter(item => item.code !== code);
    updateCartUI();
    const seatElement = document.querySelector(`circle[data-code="${code}"]`);
    if (seatElement) seatElement.classList.remove('seat-selected');
}

// --- ZOOM ---
function zoomSector(event, polygonElement) {
    event.stopPropagation();

    if (polygonElement.classList.contains('active')) {
        resetZoom();
        return;
    }

    document.body.classList.add('zoom-active');

    const container = document.getElementById('map-container');
    container.classList.add('is-zoomed');
    container.style.transform = '';
    void container.offsetWidth;

    const containerRect = container.getBoundingClientRect();
    const containerCX = containerRect.left + containerRect.width / 2;
    const containerCY = containerRect.top + containerRect.height / 2;

    const deltaX = (window.innerWidth / 2) - containerCX;
    const deltaY = (window.innerHeight / 2) - containerCY;
    const isLandscape = window.innerWidth > window.innerHeight;

    container.style.transform = isLandscape
        ? `translate(${deltaX}px, ${deltaY}px) rotate(-90deg)`
        : `translate(${deltaX}px, ${deltaY}px) rotate(0deg)`;

    const zoomLayer = document.getElementById('zoom-layer');
    const viewBoxWidth = 843;
    const viewBoxHeight = 789;

    const bbox = polygonElement.getBBox();
    const cxPerc = ((bbox.x + bbox.width / 2) / viewBoxWidth) * 100;
    const cyPerc = ((bbox.y + bbox.height / 2) / viewBoxHeight) * 100;
    const translateX_perc = 50 - cxPerc;
    const offsetY = isLandscape ? 0 : -10;
    const translateY_perc = 50 - cyPerc + offsetY;

    const cWidth = container.offsetWidth;
    const cHeight = container.offsetHeight;
    const polyLocalW = (bbox.width / viewBoxWidth) * cWidth;
    const polyLocalH = (bbox.height / viewBoxHeight) * cHeight;

    let scale;
    if (isLandscape) {
        scale = Math.min((window.innerWidth * 0.7) / polyLocalH, (window.innerHeight * 0.7) / polyLocalW);
    } else {
        scale = Math.min((window.innerWidth * 0.85) / polyLocalW, (window.innerHeight * 0.5) / polyLocalH);
    }
    scale = Math.min(Math.max(scale, 2), 10);

    document.querySelectorAll('polygon').forEach(p => p.classList.remove('active'));
    polygonElement.classList.add('active');
    document.querySelector('.header').style.opacity = '0';

    zoomLayer.style.transformOrigin = `${cxPerc}% ${cyPerc}%`;
    zoomLayer.style.transform = `translate(${translateX_perc}%, ${translateY_perc}%) scale(${scale})`;

    const sectorName = polygonElement.querySelector('title').textContent;
    let seed = 0;
    for (let i = 0; i < sectorName.length; i++) seed += sectorName.charCodeAt(i);
    const priceValue = 80 + (seed % 5) * 40;

    document.getElementById('tp-name').textContent = sectorName;
    document.getElementById('tp-price').textContent = `R$ ${priceValue},00 (Unid.)`;
    document.getElementById('ticket-panel').classList.add('show');

    generateSeats(polygonElement, sectorName, priceValue);
}

function resetZoom() {
    document.body.classList.remove('zoom-active');

    const container = document.getElementById('map-container');
    container.classList.remove('is-zoomed');
    container.style.transform = '';

    const zoomLayer = document.getElementById('zoom-layer');
    zoomLayer.style.transform = 'translate(0%, 0%) scale(1)';

    document.querySelectorAll('polygon').forEach(p => p.classList.remove('active'));
    document.getElementById('seats-layer').innerHTML = '';
    document.querySelector('.header').style.opacity = '1';
    document.getElementById('ticket-panel').classList.remove('show');
}

// --- INIT ---
document.addEventListener('DOMContentLoaded', () => {
    updateCartUI();
});

