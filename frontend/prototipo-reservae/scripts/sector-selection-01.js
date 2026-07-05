// Usuário logado: menu e avatar levam ao perfil
            const hamburger = document.getElementById("hamburger-menu");
            const loginTrigger = document.getElementById("btn-login-trigger");

            function goToProfile() {
                window.location.href = "profile.html";
            }

            hamburger.addEventListener("click", goToProfile);
            loginTrigger.addEventListener("click", goToProfile);

            // Lógica de Seleção de Bilhetes e Carrinho
            let totalCartPrice = 0;
            let totalCartQty = 0;
            const cart = {
                vip: 0,
                premium: 0,
                camarote: 0,
                pista: 0
            };

            const formatPrice = (value) => value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");

            function updateCartUI() {
                const checkoutBar = document.getElementById("checkout-bar");
                document.getElementById("total-qty").textContent = totalCartQty;
                document.getElementById("total-price").textContent = formatPrice(totalCartPrice);

                if (totalCartQty > 0) {
                    checkoutBar.classList.add("visible");
                } else {
                    checkoutBar.classList.remove("visible");
                }
            }

            // Ações dos botões + e -
            document.querySelectorAll(".btn-plus").forEach(btn => {
                btn.addEventListener("click", (e) => {
                    const id = e.target.getAttribute("data-id");
                    const price = parseInt(e.target.getAttribute("data-price"));
                    
                    cart[id]++;
                    totalCartQty++;
                    totalCartPrice += price;
                    
                    document.getElementById(`qty-${id}`).textContent = cart[id];
                    e.target.parentElement.querySelector(".btn-minus").disabled = false;
                    
                    // Highlight the list item
                    document.getElementById(`item-${id === 'camarote' ? 'camarote-esq' : id}`).classList.add("active");
                    
                    updateCartUI();
                });
            });

            document.querySelectorAll(".btn-minus").forEach(btn => {
                btn.addEventListener("click", (e) => {
                    const id = e.target.getAttribute("data-id");
                    const price = parseInt(e.target.parentElement.querySelector(".btn-plus").getAttribute("data-price"));
                    
                    if (cart[id] > 0) {
                        cart[id]--;
                        totalCartQty--;
                        totalCartPrice -= price;
                        
                        document.getElementById(`qty-${id}`).textContent = cart[id];
                        
                        if (cart[id] === 0) {
                            e.target.disabled = true;
                            document.getElementById(`item-${id === 'camarote' ? 'camarote-esq' : id}`).classList.remove("active");
                        }
                        
                        updateCartUI();
                    }
                });
            });

            // Interação entre o Mapa da Arena e a Lista
            const mapSectors = document.querySelectorAll(".map-sector");
            
            mapSectors.forEach(sector => {
                // Efeito Hover cruzado
                sector.addEventListener("mouseenter", () => {
                    let id = sector.getAttribute("data-sector");
                    if(id === "camarote-dir" || id === "camarote-esq") id = "camarote-esq";
                    const listItem = document.getElementById(`item-${id}`);
                    if(listItem) listItem.style.transform = "translateX(10px)";
                });

                sector.addEventListener("mouseleave", () => {
                    let id = sector.getAttribute("data-sector");
                    let trueId = id === "camarote-dir" || id === "camarote-esq" ? "camarote" : id;
                    const listItem = document.getElementById(`item-${id === 'camarote-dir' ? 'camarote-esq' : id}`);
                    // Remove hover state unless it has items in cart
                    if(listItem && cart[trueId] === 0) {
                        listItem.style.transform = "translateX(0)";
                    }
                });

                // Clicar no mapa simula clicar no botão de '+' da lista
                sector.addEventListener("click", () => {
                    let id = sector.getAttribute("data-sector");
                    let btnId = id === "camarote-dir" || id === "camarote-esq" ? "camarote" : id;
                    
                    // Simular click no botão plus
                    const plusBtn = document.querySelector(`.btn-plus[data-id="${btnId}"]`);
                    if(plusBtn) {
                        plusBtn.click();
                        
                        // Scroll suave até à lista no mobile
                        if(window.innerWidth <= 900) {
                            document.getElementById(`item-${id === 'camarote-dir' ? 'camarote-esq' : id}`).scrollIntoView({ behavior: 'smooth', block: 'center' });
                        }
                    }
                });
            });



