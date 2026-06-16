# Order Processing System (Event-Driven, Kafka)

A distributed, event-driven order processing system built with Spring Boot and Apache Kafka.
Four independent microservices communicate asynchronously through Kafka events — no direct
service-to-service calls — with idempotent consumers, dead-letter queues, and a saga-based
compensating transaction for failures.

## Architecture

```
Client → POST /orders → Order Service ──order-created──▶ Kafka
                                                          │
        ┌──────────────────────┬──────────────────────────┘
        ▼                      ▼                      ▼
 Inventory Service      Payment Service       Notification Service
 (reserve/release)      (charge)              (notify customer)
```

**Happy path:** order-created → inventory-reserved → order-paid → CONFIRMED + notify
**Failure path (saga):** order-failed → Order marked FAILED + Inventory releases stock + notify

## Tech Stack

- Java 21, Spring Boot 3.3
- Spring Kafka, Apache Kafka (KRaft mode)
- Spring Data JPA, PostgreSQL (one database per service)
- Docker Compose, Kafka UI
- JUnit, Mockito, Spring Kafka Test

## Services

| Service              | Port | Database        | Responsibility                       |
|----------------------|------|-----------------|--------------------------------------|
| order-service        | 8081 | orderdb         | REST entry point, order lifecycle    |
| inventory-service    | 8082 | inventorydb     | Reserve / release stock              |
| payment-service      | 8083 | paymentdb       | Process payment                      |
| notification-service | 8084 | notificationdb  | Send customer notifications          |

## Running

```bash
# 1. Start infrastructure (Kafka, Kafka UI, Postgres)
docker compose up -d

# 2. Build all modules
mvn clean install

# 3. Run each service (separate terminals)
mvn -pl order-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

- Kafka UI: http://localhost:8090
- Order Service Swagger: http://localhost:8081/swagger-ui.html

## Placing an order

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail":"test@example.com","productId":"P-100","quantity":2,"amount":499.99}'
```
