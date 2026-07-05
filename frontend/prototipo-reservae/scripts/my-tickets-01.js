// ==========================================
            // Lógica Básica do Menu
            // ==========================================
            const hamburger = document.getElementById("hamburger-menu");

            hamburger.addEventListener("click", () => {
                hamburger.classList.toggle("active");
                // Aqui você pode integrar um menu lateral futuramente se desejar
            });

            // Lógica Simples para Alternar Abas
            const tabs = document.querySelectorAll('.tab');
            tabs.forEach(tab => {
                tab.addEventListener('click', () => {
                    tabs.forEach(t => t.classList.remove('active'));
                    tab.classList.add('active');
                });
            });



