# Deployment

## Docker Compose

```bash
cp .env.example .env
docker compose up --build
```

Services:

- `app`: BuildSage Spring Boot backend.
- `postgres`: PostgreSQL database.
- `valkey`: Redis-compatible queue/cache.

## Production Notes

- Replace `JWT_SECRET`.
- Use managed PostgreSQL and Redis/Valkey.
- Put the app behind TLS termination.
- Keep `AI_PROVIDER=mock` unless a reviewed provider is configured.
- Forward `X-Correlation-Id` through gateways and load balancers.
- Configure `GITHUB_WEBHOOK_SECRET` for webhook ingestion.
- Use `AI_PROVIDER=external` with `EXTERNAL_AI_BASE_URL`, `EXTERNAL_AI_API_KEY`, and `EXTERNAL_AI_MODEL` only through environment variables or a secret manager.
- Expose `/actuator/prometheus` only to trusted monitoring infrastructure.
