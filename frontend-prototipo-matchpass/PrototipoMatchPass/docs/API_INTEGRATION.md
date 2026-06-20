# Integracao com API

## Base URL

A base padrao fica em `src/constants/api-config.js`:

```js
baseUrl: "http://localhost:8765"
```

Tambem e possivel sobrescrever em tempo de execucao antes dos scripts da pagina:

```html
<script>
  window.MATCHPASS_API_BASE_URL = "http://localhost:8765";
</script>
```

## Services

- `src/services/event-catalog.service.js`: eventos, detalhes, setores e ingressos por evento.
- `src/services/auth.service.js`: login, cadastro, recuperacao e sessao.
- `src/services/ticket.service.js`: carteira, detalhes, transferencia e validacao.
- `src/services/order.service.js`: criacao, consulta e confirmacao de pedidos.
- `src/services/user.service.js`: perfil do usuario.
- `src/services/admin.service.js`: dashboard e operacoes administrativas.

O exemplo informado foi usado como base:

```txt
http://localhost:8765/event-catalog-service/api/event/
```

No frontend, isso corresponde a:

```js
EventCatalogService.listEvents();
EventCatalogService.getEventById(eventId);
EventCatalogService.getEventSectors(eventId);
```

## DTOs

Os contratos de referencia estao em `src/types/*.dto.ts`.

Como o projeto ainda roda em HTML/JavaScript puro, esses arquivos funcionam como documentacao tipada para a integracao. Se o projeto migrar para TypeScript, eles ja podem ser importados diretamente.

## Bootstrap das paginas

As paginas receberam `data-api-page` no `<body>` e carregam:

```html
<script src="../../src/scripts/page-data.bootstrap.js"></script>
```

Esse bootstrap nao usa `type="module"` para tambem funcionar quando o prototipo e aberto diretamente via `file://`. Os arquivos em `src/services` continuam como camada organizada de referencia para quando o projeto rodar em servidor/build.

O bootstrap chama o service adequado e publica o resultado em:

```js
window.matchPassData
```

Ele tambem dispara eventos:

```js
document.addEventListener("matchpass:data-loaded", (event) => {
  console.log(event.detail.pageKey, event.detail.data);
});

document.addEventListener("matchpass:data-error", (event) => {
  console.warn(event.detail.pageKey, event.detail.error);
});
```

Quando `matchpass:data-error` acontece, a pagina tambem mostra automaticamente um toast de erro. O estilo fica em `src/styles/components/toast.css`.

Durante o carregamento, o bootstrap renderiza skeletons fieis aos componentes reais da pagina. O estilo fica em `src/styles/components/skeleton.css`, e a renderizacao acontece antes da chamada da API.

Se a chamada falhar, o skeleton e substituido por uma mensagem de erro no proprio container da pagina, com botao `Tentar novamente`. O toast tambem continua aparecendo como aviso complementar.

## Query params uteis

Algumas paginas ja leem parametros da URL:

- `?id=...` ou `?eventId=...` para detalhes/setores de evento.
- `?ticketId=...` para detalhes de ingresso.
- `?orderId=...` para pedido/sucesso/avaliacao.
