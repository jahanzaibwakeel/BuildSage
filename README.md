# BuildSage Java

BuildSage Java is a production-style Spring Boot backend for AI-powered CI/CD and development intelligence. It ingests pipeline runs, logs, deployments, incidents, and release data, then queues AI-assisted analysis for failure classification, root-cause summaries, risk scoring, postmortem drafts, and release-note generation.

Recommended GitHub repository name: `buildsage-java`.

Recommended GitHub description: `AI-powered CI/CD intelligence backend in Java 21 and Spring Boot for pipeline analysis, deployment risk scoring, incidents, and release insights.`

## Stack

- Java 21, Spring Boot 3, Spring Web, Spring Security, JPA/Hibernate
- PostgreSQL, Flyway, Valkey/Redis
- JWT authentication with roles: `ADMIN`, `DEVELOPER`, `VIEWER`
- Spring Boot Actuator and OpenAPI/Swagger
- JUnit 5, Mockito, Testcontainers
- Docker, Docker Compose, GitHub Actions

## Quick Start

```bash
cp .env.example .env
docker compose up --build
```

API docs: `http://localhost:8080/swagger-ui.html`

Health: `http://localhost:8080/actuator/health`

Demo users all use password `password`:

| Email | Role |
| --- | --- |
| `admin@buildsage.dev` | `ADMIN` |
| `dev@buildsage.dev` | `DEVELOPER` |
| `viewer@buildsage.dev` | `VIEWER` |
| `other-dev@buildsage.dev` | `DEVELOPER`, separate team |

## Local Maven Commands

```bash
./mvnw test
./mvnw verify
./mvnw spring-boot:run
```

## Example Login

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@buildsage.dev","password":"password"}'
```

More sample requests are in [http/buildsage.http](C:/Users/Dossani%20Computer/Documents/New%20project%203/http/buildsage.http).

## Project Structure

- `controller`: HTTP controllers only
- `service`: business logic, authorization, async workflows
- `domain`: JPA entities and enums
- `repository`: Spring Data repositories
- `dto`: request/response records
- `security`: JWT, user details, auth filters
- `ai`: provider abstraction, mock provider, Ollama-compatible provider
- `exception`: centralized API error handling
- `db/migration`: Flyway schema and seed data

## Known Limitations

- The default AI provider is deterministic and local-friendly. Set `AI_PROVIDER=ollama` to use an Ollama-compatible API.
- Redis/Valkey queue writes are attempted, then the app falls back to in-process async processing if Redis is unavailable.
- This is a backend portfolio/reference implementation, not a horizontally sharded production cluster.
