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
