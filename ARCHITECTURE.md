# Architecture

BuildSage Java follows a layered Spring Boot architecture:

- Controllers validate and route requests.
- Services own business workflows, authorization checks, audit events, queueing, and transaction boundaries.
- Repositories isolate database persistence.
- AI providers sit behind `AiProvider`, allowing mock, local, or external implementations.
- Flyway owns schema evolution.
- Notification delivery is optional and isolated behind a service so in-app records remain durable even if outbound webhook delivery fails.

## Key Workflows

1. A user logs in and receives a JWT.
2. A developer ingests a pipeline run with jobs and logs.
3. The API stores persistent data and returns quickly.
4. Analysis is queued in Valkey/Redis when available and dispatched to an async worker.
5. The worker loads log lines, calls the configured AI provider, stores evidence, confidence, and review status.
6. The worker creates team-scoped notifications when analysis completes.
7. Dashboard APIs aggregate recent runs, failures, incidents, and deployment risk.

## Production Concerns Demonstrated

- Explicit DTOs and response envelopes.
- Role-based access and project membership checks.
- Flyway migrations with indexes, constraints, and demo seed data.
- Structured logging with correlation IDs.
- Health/readiness endpoints.
- Prometheus metrics and OpenTelemetry-ready tracing configuration.
- Kubernetes manifests for app deployment, probes, service routing, and secret/config separation.
- Testcontainers integration tests.
