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

## Kubernetes

Example manifests live in `deploy/k8s`.

```bash
kubectl apply -f deploy/k8s/namespace.yaml
kubectl apply -f deploy/k8s/configmap.yaml
kubectl apply -f deploy/k8s/secret.example.yaml
kubectl apply -f deploy/k8s/deployment.yaml
kubectl apply -f deploy/k8s/service.yaml
```

Before using them outside a demo environment:

- Replace `secret.example.yaml` values or source them from a real secret manager.
- Point `DATABASE_URL`, `REDIS_HOST`, and `REDIS_PORT` at managed services.
- Build and publish an image matching the repository in `deployment.yaml`.
- Apply an ingress only after TLS and DNS are configured.

## Production Notes

- Replace `JWT_SECRET`.
- Use managed PostgreSQL and Redis/Valkey.
- Put the app behind TLS termination.
- Keep `AI_PROVIDER=mock` unless a reviewed provider is configured.
- Forward `X-Correlation-Id` through gateways and load balancers.
- Configure `GITHUB_WEBHOOK_SECRET` for webhook ingestion.
- Use `AI_PROVIDER=external` with `EXTERNAL_AI_BASE_URL`, `EXTERNAL_AI_API_KEY`, and `EXTERNAL_AI_MODEL` only through environment variables or a secret manager.
- Expose `/actuator/prometheus` only to trusted monitoring infrastructure.
- Configure `OTEL_EXPORTER_OTLP_ENDPOINT` when traces should be exported to an OpenTelemetry collector.
- Enable `NOTIFICATION_WEBHOOK_ENABLED` only for trusted webhook targets and rotate `NOTIFICATION_WEBHOOK_SECRET` like any other signing secret.
