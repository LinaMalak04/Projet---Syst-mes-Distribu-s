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

À venir.
