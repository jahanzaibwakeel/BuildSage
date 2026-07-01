# Testing

Run all tests:

```bash
./mvnw verify
```

On Docker Desktop for Windows, Testcontainers may need the active Linux engine pipe:

```powershell
$env:DOCKER_HOST='npipe:////./pipe/dockerDesktopLinuxEngine'
.\mvnw.cmd verify
```

Run unit tests only:

```bash
./mvnw test
```

Coverage included:

- Failure classification unit tests.
- AI provider abstraction unit test.
- Deployment risk scoring unit test.
- Protected endpoint API test.
- Invalid log ingestion API test.
- Security response envelope test.
- Viewer and cross-team authorization tests.
- Queued analysis workflow completion test.
- Idempotent pipeline ingestion test.
- Log search API test.
- Queue status API test.
- Signed webhook ingestion and bad-signature tests.
- External AI provider fallback unit test.
- Notification list and mark-read API test.
- Log archive URI, digest, and line-count ingestion assertions.
- PostgreSQL integration via Testcontainers profile.

Future improvements:

- Add mutation testing for classifier heuristics.
- Add contract tests for a real external AI provider.
- Add more authorization matrix tests across all endpoints.
