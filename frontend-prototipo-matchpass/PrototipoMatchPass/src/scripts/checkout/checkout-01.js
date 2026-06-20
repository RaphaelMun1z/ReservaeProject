// Usuário logado: menu leva ao perfil
            const hamburger = document.getElementById("hamburger-menu");

            hamburger.addEventListener("click", () => {
                window.location.href = "../user/profile.html";
            });

            // Lógica das Abas de Pagamento
            const payTabs = document.querySelectorAll('.pay-tab');
            const payContents = document.querySelectorAll('.payment-content');

            payTabs.forEach(tab => {
                tab.addEventListener('click', () => {
                    // Remove active de todas as abas e conteúdos
                    payTabs.forEach(t => t.classList.remove('active'));
                    payContents.forEach(c => c.classList.remove('active'));

                    // Adiciona active na aba clicada e no conteúdo correspondente
                    tab.classList.add('active');
                    const targetId = tab.getAttribute('data-target');
                    document.getElementById(targetId).classList.add('active');
                });
            });

            // Lógica do Cronômetro Regressivo (10 Minutos)
            let timeInSeconds = 10 * 60; 
            const countdownEl = document.getElementById('countdown');

            function updateTimer() {
                const minutes = Math.floor(timeInSeconds / 60);
                const seconds = timeInSeconds % 60;
                
                // Formata para ter sempre dois dígitos (ex: 09:05)
                const formattedMinutes = minutes.toString().padStart(2, '0');
                const formattedSeconds = seconds.toString().padStart(2, '0');
                
                countdownEl.textContent = `${formattedMinutes}:${formattedSeconds}`;

                if (timeInSeconds > 0) {
                    timeInSeconds--;
                } else {
                    // Ação quando o tempo expira
                    countdownEl.textContent = "00:00";
                    countdownEl.style.color = "var(--gray)";
                    alert("Seu tempo de reserva expirou. Por favor, inicie a compra novamente.");
                }
            }

            // Inicia o timer e atualiza a cada segundo
            setInterval(updateTimer, 1000);
            updateTimer(); // Chamada inicial imediata


            const paymentButton = document.querySelector(".summary-box .btn-checkout");
            if (paymentButton) {
                paymentButton.addEventListener("click", () => {
                    window.location.href = "success.html?orderId=mock-order";
                });
            }