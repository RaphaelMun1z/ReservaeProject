// Alternar páginas (SPA)
        function switchPage(pageId) {
            document.querySelectorAll('.page-view').forEach(el => {
                el.classList.add('hidden');
                el.classList.remove('block');
            });
            const targetPage = document.getElementById('view-' + pageId);
            if(targetPage) {
                targetPage.classList.remove('hidden');
                targetPage.classList.add('block');
            }
            document.querySelectorAll('.nav-item').forEach(el => {
                el.classList.remove('bg-brand/10', 'text-brand', 'font-semibold');
                el.classList.add('text-gray-400', 'hover:text-white', 'hover:bg-dark-hover', 'font-medium');
            });
            const activeLink = document.getElementById('nav-' + pageId);
            if(activeLink) {
                activeLink.classList.remove('text-gray-400', 'hover:text-white', 'hover:bg-dark-hover', 'font-medium');
                activeLink.classList.add('bg-brand/10', 'text-brand', 'font-semibold');
            }
            const titles = {
                'dashboard': 'Dashboard',
                'eventos': 'Eventos',
                'clientes': 'Clientes',
                'transacoes': 'Transações',
                'relatorios': 'Relatórios',
                'configuracoes': 'Configurações'
            };
            document.getElementById('header-breadcrumb').innerText = titles[pageId];
        }

        // Função para mostrar/ocultar painéis de filtro
        function toggleFilters(panelId) {
            const panel = document.getElementById(panelId);
            if (panel) {
                if (panel.classList.contains('hidden')) {
                    panel.classList.remove('hidden');
                    panel.classList.add('block'); // Força a exibição caso haja conflitos
                } else {
                    panel.classList.add('hidden');
                    panel.classList.remove('block');
                }
            }
        }

        const initialAdminView = window.location.hash.replace('#', '');
        if (initialAdminView) {
            switchPage(initialAdminView);
        }


