# CI/CD

GitHub Actions workflow: `.github/workflows/ci.yml`.

Pipeline stages:

1. Set up Java 21.
2. Run unit tests.
3. Run formatting and static analysis checks.
4. Run integration tests.
5. Build the Spring Boot jar.
6. Build a Docker image.

Local equivalents:

```bash
./mvnw test
./mvnw spotless:check
./mvnw spotbugs:check
./mvnw verify
./mvnw -DskipTests package
docker build -t buildsage-java:local .
```
