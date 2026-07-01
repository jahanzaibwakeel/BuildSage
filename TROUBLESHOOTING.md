# Troubleshooting

## Login Fails

Use password `password` for demo users. Confirm Flyway seed data ran.

## App Cannot Connect to Database

Check:

```bash
docker compose ps
docker compose logs postgres
```

## Redis/Valkey Is Down

The app logs a warning and continues with in-process async execution for local demos. Production should alert on queue/cache unavailability.

## JWT Secret Error

`JWT_SECRET` must be at least 32 characters for HMAC signing.

## Tests Need Docker

Integration tests use Testcontainers, so Docker must be running.

On Docker Desktop for Windows, set:

```powershell
$env:DOCKER_HOST='npipe:////./pipe/dockerDesktopLinuxEngine'
```

This matches the `desktop-linux` Docker context used by Docker Desktop.

## Notification Webhook Is Not Firing

Check:

- `NOTIFICATION_WEBHOOK_ENABLED=true`
- `NOTIFICATION_WEBHOOK_URL` is reachable from the app container or pod
- the receiver accepts `Content-Type: application/json`
- signature verification uses `X-BuildSage-Signature-256` and the same `NOTIFICATION_WEBHOOK_SECRET`

Webhook delivery failures are logged as warnings and do not roll back the durable in-app notification.

## Kubernetes Pod Is Not Ready

Check `/actuator/health/readiness`, database connectivity, Redis/Valkey DNS, and whether the Kubernetes secret contains a valid `JWT_SECRET`. The example manifests assume the image runs as UID `10001`.
