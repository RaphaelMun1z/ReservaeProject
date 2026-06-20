document.getElementById('transfer-form').addEventListener('submit', (e) => {
                e.preventDefault();
                alert('Transferência solicitada com sucesso! O destinatário receberá um e-mail de confirmação.');
                window.location.href = 'my-tickets.html';
            });

