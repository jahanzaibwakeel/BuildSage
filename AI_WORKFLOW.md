# AI Workflow

BuildSage keeps AI behind the `AiProvider` interface.

## Providers

- `mock`: deterministic provider for tests, demos, and offline development.
- `ollama`: local Ollama-compatible HTTP integration.

No API keys are hardcoded. Configure providers with environment variables:

```bash
AI_PROVIDER=mock
AI_PROVIDER=ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.1
```

## Log Analysis Output

Stored analysis includes:

- `status`: `QUEUED`, `PROCESSING`, `COMPLETED`, `FAILED`
- `failureType`: `TEST_FAILURE`, `BUILD_FAILURE`, `DOCKER_FAILURE`, `DEPENDENCY_FAILURE`, `ENVIRONMENT_FAILURE`, `DEPLOYMENT_FAILURE`, `UNKNOWN`
- `failedStage`
- `rootCauseSummary`
- `confidenceScore`
- `evidenceLines`
- `reviewState`: defaults to `REVIEW_REQUIRED`

The mock provider uses keyword classification so tests are stable. A real provider should still return structured output and treat AI text as reviewable, not automatically trusted.

## Queue Visibility

When analysis is requested, BuildSage stores:

- an `ai_analyses` record with `QUEUED`
- a `background_jobs` record with `QUEUED`
- a Redis/Valkey list entry when Redis is available

The worker updates the background job to `PROCESSING`, then `COMPLETED` or `FAILED`. Clients can inspect the latest queue state with:

```text
GET /api/pipeline-runs/{id}/analysis/queue
```
