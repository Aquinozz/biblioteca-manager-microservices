# Biblioteca Manager Microservices

Evolucao de uma API monolitica de gerenciamento de biblioteca para arquitetura de microservicos.

**Repositorio original:** [github.com/Aquinozz/biblioteca-manager-api](https://github.com/Aquinozz/biblioteca-manager-api)

---

## Arquitetura

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

## Servicos

| Servico | Porta | Funcao | Tecnologias |
|---------|-------|--------|-------------|
| **config-server** | 8888 | Centralizacao de configuracoes | Spring Cloud Config |
| **discovery-server** | 8761 | Service Registry | Eureka |
| **api-gateway** | 8080 | API Gateway | Spring Cloud Gateway |
| **auth-service** | 8081 | Autenticacao e autorizacao | JWT, Spring Security |
| **book-service** | 8082 | CRUD de livros e estoque | JPA, MySQL |
| **vendas-service** | 8083 | Processamento de vendas | Feign, Kafka |
| **mysql** | 3307 | Banco de dados (authdb, bookdb, vendasdb) | MySQL 8 |
| **kafka** | 9092 | Mensageria assincrona | Kafka + Zookeeper |

---

## Como rodar

### Pre-requisitos

- Docker e Docker Compose
- Java 21 (para desenvolvimento)

### Build e execucao

```bash
# Buildar todos os servicos
docker compose build

# Subir todos os servicos
docker compose up -d

# Acompanhar logs
docker compose logs -f
```

### Verificar se tudo subiu

```bash
curl http://localhost:8761/eureka/apps
```

Todos os servicos devem aparecer com status `UP`.

---

## Fluxo de uso

### 1. Login como admin

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@email.com","senha":"123456"}'
```

Salve o token retornado:

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

### 2. Criar um livro

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

### 3. Listar livros

```bash
curl http://localhost:8080/livros -H "Authorization: Bearer $TOKEN"
```

### 4. Realizar uma venda

```bash
curl -X POST http://localhost:8080/vendas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "formaPagamento": "PIX",
    "itens": [{"livroId": 1, "quantidade": 2}]
  }'
```

A venda publica um evento no Kafka. O book-service consome o evento e atualiza o estoque assincronamente.

### 5. Cancelar uma venda

```bash
curl -X DELETE http://localhost:8080/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Verificar estoque apos venda/cancelamento

```bash
curl http://localhost:8081/livros/1 -H "Authorization: Bearer $TOKEN"
```

---

## Padroes de microservicos implementados

| Padrao | Implementacao |
|--------|---------------|
| **API Gateway** | Spring Cloud Gateway como entry point unico |
| **Service Discovery** | Netflix Eureka para registro e descoberta |
| **Config Centralizada** | Spring Cloud Config Server com config-repo versionado |
| **Seguranca Distribuida** | JWT com validacao local em cada servico |
| **Comunicacao Sincrona** | OpenFeign entre vendas-service e book-service |
| **Comunicacao Assincrona** | Kafka para eventos de venda e cancelamento |
| **Circuit Breaker** | Resilience4j com fallback para degradacao graciosa |
| **Database per Service** | Um MySQL com bancos separados (authdb, bookdb, vendasdb) |

---

## Testes

```bash
# Vendas service (17 testes)
cd vendas-service && ./mvnw test

# Book service (15 testes)
cd book-service && ./mvnw test
```

**32 testes unitarios** com JUnit 5 e Mockito cobrindo:

- Regras de negocio de vendas (vender, cancelar, validacoes)
- CRUD de livros e filtros
- Validacao de tokens JWT
- Publicacao de eventos Kafka

---

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.x** (Eureka, Gateway, OpenFeign)
- **Spring Security** + **JWT** (jjwt 0.12.7)
- **Apache Kafka** + **Zookeeper**
- **Resilience4j** (Circuit Breaker)
- **MySQL 8** (um servidor, bancos separados por servico)
- **H2** (apenas nos testes, em memoria)
- **Docker** + **Docker Compose**
- **Maven**
- **JUnit 5** + **Mockito**
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)

---

## Estrutura do projeto

```
biblioteca-manager-microservices/
├── config-server/        # Config Server (Spring Cloud Config)
├── config-repo/          # Configuracoes centralizadas (YAML por servico)
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/         # Autenticacao JWT
├── book-service/         # CRUD de livros
├── discovery-server/     # Eureka Service Registry
├── vendas-service/       # Processamento de vendas
├── mysql-init/           # Script de criacao dos bancos (authdb, bookdb, vendasdb)
└── docker-compose.yml    # Orquestracao
```

---

## Proximos passos (estudo)

- [x] Observabilidade (Prometheus + Grafana)
- [x] Config Server (Spring Cloud Config)
- [x] Migrar H2 para MySQL
- [ ] Kubernetes (minikube)

---

Projeto de estudo desenvolvido por [Aquinozz](https://github.com/Aquinozz)
