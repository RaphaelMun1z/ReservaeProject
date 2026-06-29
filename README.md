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

| Categoria    | Subdomínios                                                                                         | Complexidade | Diferencial |
| :----------- | :-------------------------------------------------------------------------------------------------- | :----------- | :---------- |
| **Core**     | Inventário de Assentos e Mapa do Estádio, Reserva e Checkout (Venda), Gestão de Partidas e Eventos. | Alta         | Alto        |
| **Suporte**  | Programa de Sócio-Torcedor, Controle de Acesso (Catracas), Fraude e Cambismo.                       | Baixa        | Alto        |
| **Genérico** | Pagamentos, Autenticação e Cadastro (Identidade), Notificações.                                     | Alta         | Baixo       |

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

### Portas e Serviços em Execução

Todos os serviços, orquestradores e bancos de dados estão mapeados individualmente no ambiente para garantir a total segregação de responsabilidades e evitar conflitos.

### 1. Infraestrutura de Apoio

| Status | Componente           | Porta   | Papel                               |
| :----- | :------------------- | :------ | :---------------------------------- |
| 🟡     | Spring Cloud Config  | `8888`  | Centralização                       |
| 🟡     | Eureka Naming Server | `8761`  | Service Discovery                   |
| 🟡     | API Gateway          | `8765`  | Roteamento                          |
| 🔴     | Zipkin Server        | `9411`  | Rastreamento                        |
| 🟡     | Apache Kafka         | `29092` | Mensageria orientada a eventos      |
| 🟡     | ZooKeeper            | `22181` | Coordenação do Kafka                |
| 🟡     | Kafka UI             | `8090`  | Interface de gerenciamento do Kafka |

### 2. Microsserviços e Persistência

| Status Serviço | Status BD | Microsserviço           | Porta da Aplicação | Persistência                      | Porta BD | Database           |
| -------------- | --------- | ----------------------- | ------------------ | --------------------------------- | -------- | ------------------ |
| 🟡             | 🟡        | User Profile Service    | `8000`             | PostgreSQL                        | `5432`   | `db_identity`      |
| 🟡             | 🟡        | Event Catalog Service   | `8100`             | PostgreSQL                        | `5433`   | `db_event_catalog` |
| 🟡             | 🟡        | Inventory Service       | `8200`             | Redis (Cache / Distributed Locks) | `6379`   | —                  |
| 🟡             | 🟡        | Order Service           | `8300`             | PostgreSQL                        | `5434`   | `db_order`         |
| 🟡             | ⚪         | Payment Service         | `8400`             | —                                 | —        | —                  |
| 🟡             | 🟡        | Ticket Service          | `8500`             | PostgreSQL                        | `5435`   | `db_ticket`        |
| 🟡             | 🟡        | Notification Service    | `8600`             | MongoDB                           | `27017`  | `db_logs`          |
| 🟡             | ⚪         | Auth Service (Keycloak) | `8080`             | —                                 | —        | —                  |

---

**Legenda de Status:**

* ⚪ **Não aplicável**
* 🟡 **Em desenvolvimento**
* 🟢 **Finalizado**
* 🟠 **Manutenção**
* 🔴 **Fora do ar**

---

## 💻 Interface da Aplicação

<div align="center">
  <img src="preview4.png" width="48%" alt="Tela 1"/>
  &nbsp;&nbsp;
  <img src="preview2.png" width="48%" alt="Tela 2"/>
</div>

<br>

<div align="center">
  <img src="preview1.png" width="48%" alt="Tela 3"/>
  &nbsp;&nbsp;
  <img src="preview3.png" width="48%" alt="Tela 4"/>
</div>

---

## 🛠️ Stack Tecnológica

### Backend & Infraestrutura

* **Linguagem / Framework:** Java com Spring Boot.
* **Cloud & Roteamento:** Spring Cloud Gateway, Netflix Eureka, Spring Cloud Config.
* **Mensageria (Eventos Assíncronos):** Apache Kafka.
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
