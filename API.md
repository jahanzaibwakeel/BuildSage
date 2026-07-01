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
- `POST /api/pipeline-runs/{id}/analyze`
- `GET /api/pipeline-runs/{id}/analysis`
- `GET /api/pipeline-runs/{id}/analysis/queue`
- `POST /api/webhooks/github/projects/{id}/pipeline-runs`
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
