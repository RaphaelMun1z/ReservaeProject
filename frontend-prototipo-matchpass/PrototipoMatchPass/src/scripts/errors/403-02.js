document.addEventListener('DOMContentLoaded', () => {
            const iconContainer = document.querySelector('.group .relative.w-32');
            if(iconContainer) {
                // Dispara o shake 1 segundo após o carregamento para chamar a atenção
                setTimeout(() => {
                    iconContainer.classList.add('animate-shake');
                    setTimeout(() => {
                        iconContainer.classList.remove('animate-shake');
                    }, 500);
                }, 800);
            }
        });

