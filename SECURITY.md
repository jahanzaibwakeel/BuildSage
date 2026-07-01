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

Demo credentials are documented for local use only and should not be used in production.
