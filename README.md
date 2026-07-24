<h1 align="left">👨‍💻 Hi, I'm Aquinozz</h1>

<h3 align="left">Software Engineer | Java, Python & Scalable Systems</h3>

<p align="left">
I'm a backend-focused developer who enjoys designing and building scalable, reliable systems.
I specialize in creating robust APIs, writing clean and maintainable code, and optimizing database performance.
I also build enterprise-grade backend applications using Java and Spring.
</p>

---

<h2 align="left">🚀 What I Do</h2>

<ul>
  <li>Design and develop RESTful APIs using <b>FastAPI</b>, <b>Flask</b>, and <b>Spring Boot</b></li>
  <li>Develop backend systems with <b>Python</b> and <b>Java</b></li>
  <li>Work with relational databases (<b>PostgreSQL</b>, <b>MySQL</b>, <b>SQLite</b>)</li>
  <li>Write clean, maintainable, and well-tested backend code</li>
  <li>Containerize applications using <b>Docker</b></li>
</ul>

---

<h2 align="left">🛠 Languages & Technologies</h2>

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/sqlite/sqlite-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flask/flask-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/fastapi/fastapi-original.svg" height="40" />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" height="40" />
  <img width="12" />
  <img src="https://pngimg.com/uploads/linux/linux_PNG1.png" width="40">
</div>

---

<h2 align="left">📚 Currently Learning</h2>

<ul>
  <li>Spring Boot & Java backend architecture</li>
  <li>Advanced FastAPI & async patterns</li>
  <li>Backend system design</li>
  <li>Performance optimization & scalability</li>
</ul>

---

<h2 align="left">📊 GitHub Stats</h2>

<p align="center">
  <img src="https://github-readme-stats-sigma-five.vercel.app/api?username=Aquinozz&show_icons=true&theme=dark" />
</p>

<p align="center">
  <img src="https://github-readme-stats-sigma-five.vercel.app/api/top-langs/?username=Aquinozz&layout=compact&theme=dark" />
</p>

---

# Biblioteca Manager Microservices

Evolucao de uma API monolitica de gerenciamento de biblioteca para arquitetura de microservicos.

**Repositorio original:** [github.com/Aquinozz/biblioteca-manager-api](https://github.com/Aquinozz/biblioteca-manager-api)

---

## Arquitetura

```
┌──────────┐     ┌──────────────┐
│  Cliente  │────▶│ API Gateway  │
└──────────┘     │   (8080)     │
                 └──────┬───────┘
          ┌─────────────┼──────────────┐
          ▼             ▼              ▼
  ┌────────────┐ ┌───────────┐ ┌──────────────┐
  │Auth Service│ │Book Service│ │Vendas Service│
  │  (8081)   │ │  (8082)   │ │   (8083)    │
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
| **discovery-server** | 8761 | Service Registry | Eureka |
| **api-gateway** | 8080 | API Gateway | Spring Cloud Gateway |
| **auth-service** | 8081 | Autenticacao e autorizacao | JWT, Spring Security |
| **book-service** | 8082 | CRUD de livros e estoque | JPA, H2 |
| **vendas-service** | 8083 | Processamento de vendas | Feign, Kafka |
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
| **Seguranca Distribuida** | JWT com validacao local em cada servico |
| **Comunicacao Sincrona** | OpenFeign entre vendas-service e book-service |
| **Comunicacao Assincrona** | Kafka para eventos de venda e cancelamento |
| **Circuit Breaker** | Resilience4j com fallback para degradacao graciosa |
| **Database per Service** | Cada servico com seu proprio banco H2 |

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
- **H2 Database**
- **Docker** + **Docker Compose**
- **Maven**
- **JUnit 5** + **Mockito**
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)

---

## Estrutura do projeto

```
biblioteca-manager-microservices/
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/         # Autenticacao JWT
├── book-service/         # CRUD de livros
├── discovery-server/     # Eureka Service Registry
├── vendas-service/       # Processamento de vendas
├── data/                 # Bancos H2 (dev)
└── docker-compose.yml    # Orquestracao
```

---

## Proximos passos (estudo)

- [ ] Observabilidade (Prometheus + Grafana)
- [ ] Migrar H2 para PostgreSQL
- [ ] CI/CD com GitHub Actions
- [ ] Config Server (Spring Cloud Config)
- [ ] Kubernetes (minikube)

---

Projeto de estudo desenvolvido por [Aquinozz](https://github.com/Aquinozz)
