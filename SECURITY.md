# Security

BuildSage uses stateless JWT authentication and Spring Security.

## Roles

- `ADMIN`: system-wide access.
- `DEVELOPER`: project/team write access.
- `VIEWER`: read-only access.

## Controls

- BCrypt password hashing.
- JWT signing secret from `JWT_SECRET`.
- No hardcoded API keys.
- Project access enforced through team membership.
- CORS configured by `CORS_ALLOWED_ORIGINS`.
- Basic in-memory rate limiting per IP and URI.
- Secure error responses without stack traces.
- Correlation ID response header for incident triage.
- Signed GitHub-style webhook ingestion using `X-Hub-Signature-256` and `GITHUB_WEBHOOK_SECRET`.
- Idempotency keys for ingestion retry safety.

Demo credentials are documented for local use only and should not be used in production.

## Webhook Security

`POST /api/webhooks/github/projects/{id}/pipeline-runs` is intentionally not JWT-protected because CI providers call it machine-to-machine. It is protected by HMAC-SHA256 request signing instead.

Rules:

- Configure `GITHUB_WEBHOOK_SECRET`.
- Send `X-Hub-Signature-256: sha256=<hex-hmac>`.
- Send `X-GitHub-Delivery` when available so BuildSage can use it as an idempotency key.
- Invalid or missing signatures are rejected.
