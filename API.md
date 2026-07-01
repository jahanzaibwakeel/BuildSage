# API

All API responses use:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "timestamp": "2026-06-30T00:00:00Z"
}
```

Errors use:

```json
{
  "success": false,
  "data": null,
  "error": {"code": "VALIDATION_ERROR", "message": "Request validation failed", "details": {}},
  "timestamp": "2026-06-30T00:00:00Z"
}
```

## Endpoints

- `POST /api/auth/login`
- `POST /api/projects`
- `GET /api/projects`
- `POST /api/projects/{id}/repositories`
- `GET /api/projects/{id}/repositories`
- `GET /api/repositories/{id}`
- `POST /api/projects/{id}/pipeline-runs`
- `GET /api/projects/{id}/pipeline-runs`
- `GET /api/pipeline-runs/{id}`
- `GET /api/pipeline-runs/{id}/logs`
- `GET /api/pipeline-runs/{id}/logs/archive`
- `POST /api/pipeline-runs/{id}/analyze`
- `GET /api/pipeline-runs/{id}/analysis`
- `GET /api/pipeline-runs/{id}/analysis/queue`
- `POST /api/webhooks/github/projects/{id}/pipeline-runs`
- `GET /api/notifications`
- `POST /api/notifications/{id}/read`
- `POST /api/projects/{id}/deployments`
- `POST /api/deployments/{id}/risk-score`
- `POST /api/incidents`
- `POST /api/incidents/{id}/postmortem`
- `POST /api/projects/{id}/release-notes`
- `GET /api/projects/{id}/metrics`

Swagger UI is available at `/swagger-ui.html`.

Pagination uses Spring parameters: `page`, `size`, and `sort`.

OpenAPI declares JWT bearer authentication as `bearerAuth`.

## Idempotent Pipeline Ingestion

Authenticated ingestion accepts an optional `Idempotency-Key` header. The request body also supports an optional `idempotencyKey` field. If a project already has a run with that key, the API returns the original run instead of creating a duplicate.

```bash
curl -X POST http://localhost:8080/api/projects/{projectId}/pipeline-runs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: github-delivery-123" \
  -H "Content-Type: application/json" \
  -d '{"externalId":"gh-123","branch":"main","commitSha":"abc","status":"FAILED","startedAt":"2026-07-01T00:00:00Z","logs":["stage=test","AssertionError"]}'
```

The request body also accepts `logArchiveUri`, which can point to an object-storage location such as `s3://bucket/key`, `gs://bucket/key`, `minio://bucket/key`, or HTTPS. BuildSage stores this URI, the log line count, and a SHA-256 digest of ingested log lines.

`GET /api/pipeline-runs/{id}/logs/archive` returns archive readiness metadata:

```json
{
  "pipelineRunId": "30000000-0000-0000-0000-000000000001",
  "archived": true,
  "storageProvider": "S3",
  "archiveUri": "s3://buildsage-demo/logs/run.txt",
  "digestAlgorithm": "SHA-256",
  "digestSha256": "abc...",
  "lineCount": 42
}
```

## Log Search

`GET /api/pipeline-runs/{id}/logs` supports:

- `q`: case-insensitive text search.
- `fromLine`: minimum line number.
- `toLine`: maximum line number.
- `page`, `size`, `sort`: pagination.

## Queue Status

`GET /api/pipeline-runs/{id}/analysis/queue` returns the latest analysis status, background job status, job message, and Redis queue depth when Redis is reachable.

## Signed GitHub-Style Webhook

`POST /api/webhooks/github/projects/{id}/pipeline-runs` is not JWT-protected, but it requires `X-Hub-Signature-256`. The signature is `sha256=` plus an HMAC-SHA256 of the raw request body using `GITHUB_WEBHOOK_SECRET`.

`X-GitHub-Delivery` is used as the idempotency key when present.

## Notifications

Users can list and mark their own in-app notifications:

```text
GET /api/notifications
POST /api/notifications/{id}/read
```

When `NOTIFICATION_WEBHOOK_ENABLED=true`, newly created notifications are also delivered to `NOTIFICATION_WEBHOOK_URL` with `X-BuildSage-Event: notification.created`. If `NOTIFICATION_WEBHOOK_SECRET` is configured, BuildSage signs the JSON payload with `X-BuildSage-Signature-256`.
