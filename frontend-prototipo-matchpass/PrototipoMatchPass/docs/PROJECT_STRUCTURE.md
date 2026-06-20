# Estrutura do Projeto

## Entrada

- `index.html`: pagina principal do prototipo.

## Paginas

- `pages/public`: paginas publicas e de descoberta, como home, shows e club VIP.
- `pages/auth`: login, cadastro e recuperacao de senha.
- `pages/checkout`: selecao de setor, checkout, sucesso e avaliacao.
- `pages/user`: carteira, perfil, suporte, transferencia e detalhes do ingresso.
- `pages/admin`: painel administrativo, configuracoes, scanner e paginas admin futuras.
- `pages/status`: estados temporarios, como loading.
- `pages/errors`: paginas de erro HTTP.

## Assets

- `assets/images`: imagens do prototipo.
- `assets/videos`: videos do prototipo.

## CSS e JavaScript

- `src/styles/index.css`: estilos da pagina principal.
- `src/styles/<grupo>/<pagina>.css`: estilos especificos das paginas em `pages`.
- `src/scripts/index-01.js`: scripts da pagina principal.
- `src/scripts/<grupo>/<pagina>-01.js`: scripts especificos das paginas em `pages`.
- Quando uma pagina tem mais de um bloco de script, os arquivos seguem a ordem original: `-01.js`, `-02.js`, etc.

## Codigo futuro

- `src/api`: clientes HTTP, configuracao de base URL e interceptors.
- `src/services`: servicos de dominio que consomem `src/api`.
- `src/types`: DTOs/interfaces para os contratos da API.
- `src/components`: componentes reutilizaveis.
- `src/layouts`: estruturas de tela compartilhadas.
- `src/styles`: estilos globais e tokens visuais.
- `src/scripts`: scripts de interacao do front-end.
- `src/utils`: funcoes utilitarias puras.
- `src/constants`: constantes de rotas, mensagens e configuracoes.
- `src/data`: mocks, fixtures e dados temporarios.
- `src/hooks`: hooks reutilizaveis caso o prototipo evolua para React/Vue/Svelte.
