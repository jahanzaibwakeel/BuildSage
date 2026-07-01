# Database

PostgreSQL is the persistent system of record. Flyway migrations live in `src/main/resources/db/migration`.

## Tables

- `users`
- `teams`
- `team_members`
- `projects`
- `repositories`
- `pipeline_runs`
- `pipeline_jobs`
- `pipeline_logs`
- `test_failures`
- `deployments`
- `incidents`
- `incident_events`
- `release_notes`
- `ai_analyses`
- `background_jobs`
- `notifications`
- `audit_logs`

## Migrations

Run migrations automatically at application startup:

```bash
./mvnw spring-boot:run
```

Or through Docker Compose:

```bash
docker compose up --build
```

`V1__initial_schema.sql` defines tables, constraints, indexes, and foreign keys. `V2__seed_demo_data.sql` adds demo users, a team, a project, and repository metadata. `V3__pipeline_idempotency_and_search.sql` adds pipeline-run idempotency keys, a partial unique index for duplicate protection, and indexes for log search and risk queries.

## Idempotency

`pipeline_runs.idempotency_key` is nullable, but unique per project when present. This lets CI providers safely retry webhook or ingestion calls without duplicating pipeline runs.
