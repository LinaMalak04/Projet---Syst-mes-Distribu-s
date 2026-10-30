# Projet de synthèse — Spring Boot, Spring Cloud, Agentic AI

Application bancaire basée sur une architecture micro-services.

## Partie 1 — Micro-services et Spring Cloud

| Module              | Port | Rôle                                                                  |
|---------------------|------|-----------------------------------------------------------------------|
| `discovery-service` | 8761 | Annuaire des services (Eureka Server)                                 |
| `gateway-service`   | 8888 | Point d'entrée unique (Spring Cloud Gateway, routes dynamiques)       |
| `customer-service`  | 8081 | Gestion des clients                                                   |
| `ebank-service`     | 8082 | Gestion des comptes bancaires et des opérations (débit, crédit, virement) |

`ebank-service` récupère les informations du client auprès de `customer-service` avec **OpenFeign**.
L'appel est protégé par un **circuit breaker Resilience4J** : si `customer-service` est indisponible,
un client par défaut (`Not Available`) est renvoyé au lieu d'une erreur.

### Technologies

Java 21, Spring Boot 3.5, Spring Cloud 2025.0 (Eureka, Gateway, OpenFeign, Resilience4J),
Spring Data JPA, H2, Lombok, springdoc-openapi (Swagger UI).

### Démarrage

Démarrer les services dans cet ordre (depuis IntelliJ ou en ligne de commande) :

```bash
mvn -pl discovery-service spring-boot:run
mvn -pl customer-service spring-boot:run
mvn -pl ebank-service spring-boot:run
mvn -pl gateway-service spring-boot:run
```

### Tests

| Outil            | URL                                              |
|------------------|--------------------------------------------------|
| Eureka dashboard | http://localhost:8761                            |
| Swagger UI       | http://localhost:8081/swagger-ui.html (clients)  |
| Swagger UI       | http://localhost:8082/swagger-ui.html (comptes)  |
| Console H2       | http://localhost:8081/h2-console, http://localhost:8082/h2-console |
| Circuit breaker  | http://localhost:8082/actuator/circuitbreakers   |

Via la gateway : `http://localhost:8888/customer-service/customers` et
`http://localhost:8888/ebank-service/accounts`.

Le fichier [`requests.http`](requests.http) contient les requêtes prêtes à exécuter dans IntelliJ
(elles peuvent aussi être rejouées dans Postman).

### API

**customer-service**

| Méthode | URL                              | Description          |
|---------|----------------------------------|----------------------|
| GET     | `/customers`                     | Liste des clients    |
| GET     | `/customers/{id}`                | Un client            |
| GET     | `/customers/search?keyword=`     | Recherche par nom    |
| POST    | `/customers`                     | Créer un client      |
| PUT     | `/customers/{id}`                | Modifier un client   |
| DELETE  | `/customers/{id}`                | Supprimer un client  |

**ebank-service**

| Méthode | URL                                  | Description                         |
|---------|--------------------------------------|-------------------------------------|
| GET     | `/accounts`                          | Liste des comptes (avec le client)  |
| GET     | `/accounts/{accountId}`              | Un compte                           |
| GET     | `/accounts/customer/{customerId}`    | Comptes d'un client                 |
| POST    | `/accounts`                          | Créer un compte                     |
| DELETE  | `/accounts/{accountId}`              | Supprimer un compte                 |
| GET     | `/accounts/{accountId}/operations`   | Historique des opérations           |
| POST    | `/accounts/{accountId}/debit`        | Débiter un compte                   |
| POST    | `/accounts/{accountId}/credit`       | Créditer un compte                  |
| POST    | `/accounts/transfer`                 | Virement entre deux comptes         |

## Partie 2 — Agent IA, Spring AI et MCP

Un nouveau micro-service, `chatbot-service`, contient un **agent IA** construit avec **Spring AI**.
L'agent ne connaît pas la base de données : il utilise les **outils MCP** exposés par
`ebank-service` et `customer-service` pour lire et modifier les données réelles.
Il est accessible par une API REST et par un **bot Telegram**.

```
Telegram  ──►  chatbot-service (Spring AI, ChatClient + mémoire)  ──►  LLM (Groq, gpt-oss-120b)
                      │  MCP client (Streamable HTTP)
                      ├──►  ebank-service    /mcp   (7 outils : comptes, opérations, débit, crédit, virement)
                      └──►  customer-service /mcp   (3 outils : clients, recherche)
```

| Module              | Port | Rôle                                                                 |
|---------------------|------|----------------------------------------------------------------------|
| `chatbot-service`   | 8087 | Agent IA (Spring AI) : client MCP, mémoire de conversation, bot Telegram |
| `ebank-service`     | 8082 | + serveur MCP sur `/mcp`                                             |
| `customer-service`  | 8081 | + serveur MCP sur `/mcp`                                             |

### Serveurs MCP

Les méthodes annotées `@Tool` (classes `EbankMcpTools` et `CustomerMcpTools`) sont publiées par
`spring-ai-starter-mcp-server-webmvc` avec le protocole **Streamable HTTP**
(`spring.ai.mcp.server.protocol=STREAMABLE`).

| Service            | Outils                                                                                          |
|--------------------|-------------------------------------------------------------------------------------------------|
| `ebank-service`    | `getAllAccounts`, `getAccountById`, `getCustomerAccounts`, `getAccountOperations`, `creditAccount`, `debitAccount`, `transfer` |
| `customer-service` | `getAllCustomers`, `getCustomerById`, `searchCustomers`                                         |

### Agent (chatbot-service)

- `BankAgent` : `ChatClient` avec un message système, les outils MCP des deux services
  (`ToolCallbackProvider` fourni par `spring-ai-starter-mcp-client`) et une mémoire des
  20 derniers messages par conversation (`MessageChatMemoryAdvisor`).
- LLM : l'API de **Groq** est compatible OpenAI, on utilise donc `spring-ai-starter-model-openai`
  avec `spring.ai.openai.base-url=https://api.groq.com/openai` et le modèle `openai/gpt-oss-120b`.
- `ChatRestController` : `GET /chat?query=...&conversationId=...`
- `TelegramBot` : bot en *long polling* (`telegrambots-springboot-longpolling-starter`),
  une mémoire par chat Telegram. Il ne démarre que si un token est fourni.

### Démarrage

1. Créer une clé API sur [console.groq.com](https://console.groq.com/keys).
2. (Optionnel) Créer un bot avec **@BotFather** sur Telegram (`/newbot`) et récupérer son token.
3. Démarrer `discovery-service`, `customer-service`, `ebank-service`, puis `chatbot-service`
   (le client MCP se connecte aux deux serveurs au démarrage) :

```bash
# PowerShell : $env:GROQ_API_KEY="gsk_..." ; $env:TELEGRAM_BOT_TOKEN="123456:ABC..."
export GROQ_API_KEY=gsk_...
export TELEGRAM_BOT_TOKEN=123456:ABC...   # optionnel
mvn -pl chatbot-service spring-boot:run
```

Dans IntelliJ, les deux variables se mettent dans *Run > Edit Configurations > Environment variables*.

### Tests

| Test                    | URL / action                                                              |
|-------------------------|---------------------------------------------------------------------------|
| Chat (direct)           | http://localhost:8087/chat?query=Quels sont les comptes de Lina ?          |
| Chat (via la gateway)   | http://localhost:8888/chatbot-service/chat?query=Liste les clients         |
| Telegram                | Écrire au bot, par exemple « Fais un virement de 500 MAD du compte X vers Y » |
| Outils MCP              | MCP Inspector (`npx @modelcontextprotocol/inspector`) sur http://localhost:8082/mcp |

Exemples de questions : « Quels clients s'appellent Salma ? », « Donne le solde des comptes du
client 2 », « Crédite 1000 MAD sur le compte courant de Yassine », « Quelles sont les dernières
opérations de ce compte ? » (la mémoire garde le contexte).
