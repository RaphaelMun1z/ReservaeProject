# MatchPass Project

O MatchPass é uma plataforma focada na venda de ingressos para partidas de futebol, priorizando a experiência do usuário, escalabilidade e alta resiliência. O sistema é construído sob uma arquitetura modular que facilita a manutenção e a integração contínua de novas funcionalidades.

---

## 🎯 Sobre o Projeto
* **Domínio Central:** Venda de Ingressos Online Para Partidas de Futebol.
* **Versão Atual:** `v0.0.1`

## 🏗️ Arquitetura
O sistema utiliza uma abordagem de **Microsserviços**, permitindo a independência entre contextos (Bounded Contexts) e garantindo que o sistema seja facilmente escalável e tolerante a falhas. 

### Definição de Domínios
Com base no mapeamento de subdomínios, o sistema é dividido nas seguintes categorias:

| Categoria | Subdomínios | Complexidade | Diferencial |
| :--- | :--- | :--- | :--- |
| **Core** | Inventário de Assentos e Mapa do Estádio, Reserva e Checkout (Venda), Gestão de Partidas e Eventos. | Alta | Alto |
| **Suporte** | Programa de Sócio-Torcedor, Controle de Acesso (Catracas), Fraude e Cambismo. | Baixa | Alto |
| **Genérico** | Pagamentos, Autenticação e Cadastro (Identidade), Notificações. | Alta | Baixo |

### Mapa de Contexto e Subdomínios (DDD)
<div align="center">
  <img src="./DDD_v0.0.1.png" width="49%" alt="Mapa de Contextos DDD">
  <img src="./GraficoCategoriaSubdominio.png" width="49%" alt="Gráfico de Categorias e Subdomínios">
</div>

### Desenho da Arquitetura
A topologia do sistema segue uma arquitetura baseada em eventos e persistência poliglota, com as seguintes camadas:
<div align="center">
  <img src="./Arquitetura.png" width="100%" alt="Arquitetura de Microsserviços">
</div>

### Portas e Serviços Em Execução
Todos os serviços, orquestradores e bancos de dados estão mapeados individualmente no ambiente para garantir a total segregação de responsabilidades e evitar conflitos.
### 1. Infraestrutura de Apoio
| Status | Componente | Porta | Papel |
| :--- | :--- | :--- | :--- |
| 🟡 | Spring Cloud Config | `8888` | Centralização |
| 🔴 | Eureka Naming Server | `8761` | Service Discovery |
| 🔴 | API Gateway | `8765` | Roteamento |
| 🔴 | Zipkin Server | `9411` | Rastreamento |
| 🔴 | RabbitMQ | `5672` | Mensageria AMQP |

### 2. Microsserviços e Persistência
| Status | Microsserviço | Porta | Banco de Dados (Porta) |
| :--- | :--- | :--- | :--- |
| 🟡 | Event Catalog Service | `8080` | PostgreSQL `5432` - db_event_catalog |
| 🟡 | Inventory Service | `8100` | Redis `6379` - Cache/Locks |
| 🟡 | Order Service | `8200` | PostgreSQL `5433` - db_order |
| 🟡 | Ticket Service | `8300` | PostgreSQL `5434` - db_ticket |
| 🔴 | Payment Service | `8400` | PostgreSQL `5432` - db_payment |
| 🔴 | Notification Service| `8500` | MongoDB `27017` - db_logs |
| 🔴 | Identity Service | `8600` | PostgreSQL `5432` - db_identity |

---
**Legenda de Status:**
* 🟡 **Em desenvolvimento**
* 🟢 **Finalizado**
* 🟠 **Manutenção**
* 🔴 **Ainda não iniciado**

---

## 💻 Interface da Aplicação

<div align="center">
  <img src="https://via.placeholder.com/600x338/000000/FFFFFF?text=Tela+Inicial+(Home)" width="48%" alt="Tela Inicial"/>
  &nbsp;&nbsp;
  <img src="https://via.placeholder.com/600x338/000000/FFFFFF?text=Catálogo+de+Partidas" width="48%" alt="Catálogo de Partidas"/>
</div>
<br>
<div align="center">
  <img src="https://via.placeholder.com/600x338/000000/FFFFFF?text=Reserva+de+Assentos" width="48%" alt="Reserva de Assentos"/>
  &nbsp;&nbsp;
  <img src="https://via.placeholder.com/600x338/000000/FFFFFF?text=Checkout+e+Pagamento" width="48%" alt="Checkout e Pagamento"/>
</div>

---

## 🛠️ Stack Tecnológica

### Backend & Infraestrutura
* **Linguagem / Framework:** Java com Spring Boot.
* **Cloud & Roteamento:** Spring Cloud Gateway, Netflix Eureka, Spring Cloud Config.
* **Mensageria (Eventos Assíncronos):** RabbitMQ.
* **Observabilidade:** Zipkin Server (Distributed Tracing).

### Frontend
* **Tecnologias:** Web App construído em Angular, comunicando-se via requisições HTTP/REST.

### Bancos de Dados (Persistência Poliglota)
O projeto utiliza a estratégia de um banco de dados independente por serviço, otimizando a escolha do SGBD:
* **PostgreSQL:** Bancos relacionais para usuários (Identity), partidas (Catalog), pedidos (Checkout), transações (Payment) e ingressos (Ticket).
* **Redis:** Armazenamento em memória chave-valor para *locks* e alta concorrência no Inventory Service.
* **MongoDB:** Banco orientado a documentos para persistência de *logs* no Notification Service.

---

## 🚀 Como Executar

### Pré-requisitos
...

### Passo a Passo
...
