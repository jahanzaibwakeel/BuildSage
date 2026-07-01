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
