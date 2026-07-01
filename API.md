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
- `POST /api/projects/{id}/deployments`
- `POST /api/deployments/{id}/risk-score`
- `POST /api/incidents`
- `POST /api/incidents/{id}/postmortem`
- `POST /api/projects/{id}/release-notes`
- `GET /api/projects/{id}/metrics`

Swagger UI is available at `/swagger-ui.html`.

Pagination uses Spring parameters: `page`, `size`, and `sort`.

OpenAPI declares JWT bearer authentication as `bearerAuth`.
