// ==========================================
            // Usuário logado: menu leva ao perfil
            // ==========================================
            const hamburger = document.getElementById("hamburger-menu");

            hamburger.addEventListener("click", () => {
                window.location.href = "../user/profile.html";
            });

            // ==========================================
            // Filtros Estéticos (Interação básica)
            // ==========================================
            const filterBtns = document.querySelectorAll('.filter-btn');
            filterBtns.forEach(btn => {
                btn.addEventListener('click', () => {
                    filterBtns.forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');
                });
            });

