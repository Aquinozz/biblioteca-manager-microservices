# Biblioteca Manager Microservices

Evolution of a monolithic library management API to a microservices architecture.

**Original repository:** [github.com/Aquinozz/biblioteca-manager-api](https://github.com/Aquinozz/biblioteca-manager-api)

---

## Architecture

```
┌──────────┐     ┌──────────────┐
│  Cliente  │──▶│ API Gateway  │
└──────────┘     │   (8080)     │
                 └──────┬───────┘
          ┌─────────────┼──────────────┐
          ▼             ▼              ▼
  ┌────────────┐ ┌───────────┐ ┌──────────────┐
  │Auth Service│ │Book Service││Vendas Service│
  │  (8081)   │  │  (8082)    ││   (8083)     │
  └────────────┘ └───────────┘ └──────┬───────┘
                                      │ Kafka
                                      ▼
                               ┌───────────┐
                               │Book Service│
                               │ (estoque)  │
                               └───────────┘

                    ┌──────────────────┐
                    │ Discovery Server │
                    │    (Eureka)      │
                    │     (8761)       │
                    └──────────────────┘
```

---

## Services

| Service | Port | Role | Technologies |
|---------|-------|--------|-------------|
| **config-server** | 8888 | Centralized configuration | Spring Cloud Config |
| **discovery-server** | 8761 | Service registry | Eureka |
| **api-gateway** | 8080 | API Gateway | Spring Cloud Gateway |
| **auth-service** | 8081 | Authentication and authorization | JWT, Spring Security |
| **book-service** | 8082 | Book and stock CRUD | JPA, MySQL |
| **vendas-service** | 8083 | Sales processing | Feign, Kafka |
| **mysql** | 3307 | Database (authdb, bookdb, vendasdb) | MySQL 8 |
| **kafka** | 9092 | Async messaging | Kafka + Zookeeper |

---

## How to run

### Prerequisites

- Docker and Docker Compose
- Java 21 (for development)

### Build and run

```bash
# Build all services
docker compose build

# Start all services
docker compose up -d

# Follow logs
docker compose logs -f
```

### Verify everything is up

```bash
curl http://localhost:8761/eureka/apps
```

All services should appear with status `UP`.

---

## Usage flow

### 1. Login as admin

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@email.com","senha":"123456"}'
```

Save the returned token:

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

### 2. Create a book

```bash
curl -X POST http://localhost:8080/livros \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "1984",
    "autor": "George Orwell",
    "descricao": "Distopia classica",
    "anoCriacao": 1949,
    "preco": 29.90,
    "quantidade": 10,
    "categoria": "FICCAO_CIENTIFICA"
  }'
```

### 3. List books

```bash
curl http://localhost:8080/livros -H "Authorization: Bearer $TOKEN"
```

### 4. Make a sale

```bash
curl -X POST http://localhost:8080/vendas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "formaPagamento": "PIX",
    "itens": [{"livroId": 1, "quantidade": 2}]
  }'
```

The sale publishes an event to Kafka. The book-service consumes the event and updates stock asynchronously.

### 5. Cancel a sale

```bash
curl -X DELETE http://localhost:8080/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Check stock after sale/cancellation

```bash
curl http://localhost:8081/livros/1 -H "Authorization: Bearer $TOKEN"
```

---

## Implemented microservices patterns

| Pattern | Implementation |
|--------|---------------|
| **API Gateway** | Spring Cloud Gateway as the single entry point |
| **Service Discovery** | Netflix Eureka for registration and discovery |
| **Centralized Config** | Spring Cloud Config Server with a versioned config-repo |
| **Distributed Security** | JWT validated locally in each service |
| **Synchronous Communication** | OpenFeign between vendas-service and book-service |
| **Asynchronous Communication** | Kafka for sale and cancellation events |
| **Circuit Breaker** | Resilience4j with fallback for graceful degradation |
| **Database per Service** | A single MySQL instance with separate databases (authdb, bookdb, vendasdb) |

---

## Tests

```bash
# Vendas service (17 tests)
cd vendas-service && ./mvnw test

# Book service (15 tests)
cd book-service && ./mvnw test
```

**32 unit tests** with JUnit 5 and Mockito covering:

- Sales business rules (sell, cancel, validations)
- Book CRUD and filters
- JWT token validation
- Kafka event publishing

---

## Technologies

- **Java 21**
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.x** (Eureka, Gateway, OpenFeign)
- **Spring Security** + **JWT** (jjwt 0.12.7)
- **Apache Kafka** + **Zookeeper**
- **Resilience4j** (Circuit Breaker)
- **MySQL 8** (single server, separate databases per service)
- **H2** (only in tests, in-memory)
- **Docker** + **Docker Compose**
- **Maven**
- **JUnit 5** + **Mockito**
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)

---

## Project structure

```
biblioteca-manager-microservices/
├── config-server/        # Config Server (Spring Cloud Config)
├── config-repo/          # Centralized configuration (YAML per service)
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/         # JWT authentication
├── book-service/         # Book CRUD
├── discovery-server/     # Eureka Service Registry
├── vendas-service/       # Sales processing
├── mysql-init/           # Database creation scripts (authdb, bookdb, vendasdb)
└── docker-compose.yml    # Orchestration
```

---

## Next steps (study)

- [x] Observability (Prometheus + Grafana)
- [x] Config Server (Spring Cloud Config)
- [x] Migrate H2 to MySQL
- [ ] Kubernetes (minikube)

---

Study project developed by [Aquinozz](https://github.com/Aquinozz)