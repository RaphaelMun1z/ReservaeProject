// Elementos do DOM
        const laser = document.getElementById('laser');
        const flashOverlay = document.getElementById('flash-overlay');
        const bgDimmer = document.getElementById('bg-dimmer');
        const stateIdle = document.getElementById('state-idle');
        const stateSuccess = document.getElementById('state-success');
        const stateError = document.getElementById('state-error');
        const feedbackPanel = document.getElementById('feedback-panel');

        // Impede duplo scan acidental
        let isScanning = false;

        function resetState() {
            stateIdle.classList.remove('hidden');
            stateIdle.classList.add('flex');
            
            stateSuccess.classList.add('hidden');
            stateSuccess.classList.remove('flex');
            
            stateError.classList.add('hidden');
            stateError.classList.remove('flex');
            
            laser.style.display = 'block';
            bgDimmer.classList.remove('opacity-100');
            bgDimmer.classList.add('opacity-0');
            
            feedbackPanel.classList.remove('border-emerald-500/50', 'border-rose-500/50');
            feedbackPanel.classList.add('border-dark-border');
            
            isScanning = false;
        }

        function simulateScan() {
            if (isScanning) return;
            isScanning = true;

            // Efeito visual de captura (Flash)
            flashOverlay.classList.add('scan-flash');
            setTimeout(() => { flashOverlay.classList.remove('scan-flash'); }, 300);

            // Esconde o laser enquanto exibe o resultado
            laser.style.display = 'none';

            // Escurece o fundo
            bgDimmer.classList.remove('opacity-0');
            bgDimmer.classList.add('opacity-100');

            // Lógica de simulação (70% sucesso, 30% erro)
            const isSuccess = Math.random() > 0.3;

            // Esconde estado idle
            stateIdle.classList.add('hidden');
            stateIdle.classList.remove('flex');

            feedbackPanel.classList.remove('border-dark-border');

            if (isSuccess) {
                // Estado de Sucesso
                feedbackPanel.classList.add('border-emerald-500/50');
                stateSuccess.classList.remove('hidden');
                stateSuccess.classList.add('flex');
                
                // Emite um beep ou feedback háptico em apps reais
                if (navigator.vibrate) navigator.vibrate([100, 50, 100]);
                
            } else {
                // Estado de Erro
                feedbackPanel.classList.add('border-rose-500/50');
                stateError.classList.remove('hidden');
                stateError.classList.add('flex');
                
                // Emite um beep de erro / vibração longa
                if (navigator.vibrate) navigator.vibrate(400);
            }

            // Retorna ao estado de leitura após 3 segundos
            setTimeout(() => {
                resetState();
            }, 3000);
        }

        // Inicializa o estado
        resetState();



