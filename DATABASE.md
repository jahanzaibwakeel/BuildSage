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

`V1__initial_schema.sql` defines tables, constraints, indexes, and foreign keys. `V2__seed_demo_data.sql` adds demo users, a team, a project, and repository metadata. `V3__pipeline_idempotency_and_search.sql` adds pipeline-run idempotency keys, a partial unique index for duplicate protection, and indexes for log search and risk queries. `V4__log_archive_and_notification_indexes.sql` adds log archive metadata, log digests, line counts, and notification indexes.

## Idempotency

`pipeline_runs.idempotency_key` is nullable, but unique per project when present. This lets CI providers safely retry webhook or ingestion calls without duplicating pipeline runs.

## Log Archive Metadata

Pipeline runs store:

- `log_archive_uri`: optional pointer to S3/GCS/MinIO/HTTPS log storage.
- `log_digest_sha256`: SHA-256 digest of ingested log lines.
- `log_line_count`: count of stored log lines.

This keeps BuildSage ready for large-log object storage without forcing object storage into local development.
