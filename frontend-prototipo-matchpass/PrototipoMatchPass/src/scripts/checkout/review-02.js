let currentRating = 0;

        function selectRating(rating) {
            currentRating = rating;
            const buttons = document.querySelectorAll('.rating-btn');
            const textArea = document.getElementById('feedback-text-area');
            const submitBtn = document.getElementById('btn-submit-feedback');

            // Atualiza opacidade e tamanho dos emojis
            buttons.forEach(btn => {
                const btnRating = parseInt(btn.getAttribute('data-rating'));
                if (btnRating === rating) {
                    btn.classList.remove('grayscale', 'opacity-40');
                    btn.classList.add('opacity-100', 'scale-110');
                } else {
                    btn.classList.add('grayscale', 'opacity-40');
                    btn.classList.remove('opacity-100', 'scale-110');
                }
            });

            // Revela a caixa de texto
            textArea.classList.remove('hidden');

            // Habilita o botão
            submitBtn.disabled = false;
            submitBtn.classList.remove('bg-dark-hover', 'text-gray-500', 'cursor-not-allowed');
            submitBtn.classList.add('bg-brand', 'text-white', 'hover:bg-brand-dark', 'shadow-[0_10px_20px_rgba(255,98,77,0.2)]');
        }

        function submitFeedback() {
            const formContainer = document.getElementById('evaluation-form');
            const successContainer = document.getElementById('evaluation-success');

            // Efeito visual de troca
            formContainer.style.display = 'none';
            successContainer.style.display = 'flex';
            successContainer.classList.remove('hidden');
        }

