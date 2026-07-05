// Interação Menu Mobile
            const hamburger = document.getElementById("hamburger-menu");
            hamburger.addEventListener("click", () => {
                hamburger.classList.toggle("active");
            });

            // Lógica das Abas do Perfil
            const tabs = document.querySelectorAll('.nav-tab');
            const contents = document.querySelectorAll('.tab-content');

            tabs.forEach(tab => {
                tab.addEventListener('click', () => {
                    // Desativar todas as abas e conteúdos
                    tabs.forEach(t => t.classList.remove('active'));
                    contents.forEach(c => c.classList.remove('active'));

                    // Ativar aba clicada
                    tab.classList.add('active');
                    const targetId = tab.getAttribute('data-target');
                    document.getElementById(targetId).classList.add('active');
                });
            });



