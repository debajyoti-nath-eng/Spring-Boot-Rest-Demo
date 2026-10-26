# Architecture

## Purpose and technology

This project is a small Spring Boot 3.5.16 REST API for managing `Item` resources. It targets Java 21 and separates HTTP handling, transactional operations, persistence, and transport models. The application starts from `SpringBootRestDemoApplication`; Spring Boot selects the `local` profile by default.

## Components

```text
HTTP request
    |
SecurityHeadersFilter
    |
Spring MVC
    |-- /api/** -> ClientTypeHeaderInterceptor -> ItemController
    |                                             |
    |                                         ItemService
    |                                             |
    |                                      ItemRepository
    |                                             |
    |                                         ItemEntity
    |
    `-- /actuator/health -> Actuator
```

- **Configuration (`config`)**: `WebConfig` registers `ClientTypeHeaderInterceptor` for `/api/**`. `SecurityHeadersFilter` adds response headers across the application.
- **Controller (`controller`)**: `ItemController` maps CRUD routes, validates request DTOs, delegates to the service, and returns the HTTP response. Creation returns `201 Created`, the created item, and a `Location` header.
- **Service (`service`, `service.impl`)**: `ItemService` defines item operations. `ItemServiceImpl` implements them, orders list results by ID, converts entities to API records, and applies read-only transactions by default with write transactions for create, update, and delete.
- **Repository (`repository`)**: `ItemRepository` extends `JpaRepository<ItemEntity, Long>` and supplies standard persistence operations.
- **Persistence model (`entity`)**: `ItemEntity` maps to the `items` table and converts itself to the API representation.
- **Transport models (`dto`)**: `Item` is the response record; `ItemRequest` is used for create and replacement requests. The request name must be nonblank, and both fields have a maximum length of 255 characters.
- **Error handling (`exception`)**: `ApiExceptionHandler` maps missing items to `404 Not Found` and Bean Validation failures to `400 Bad Request`, using Spring `ProblemDetail`. Validation responses include an `errors` object keyed by field.

## Request processing

For `/api/**`, the security header filter first sets defensive response headers. The MVC interceptor then requires `X-Client-Type` to be `UI` or `POSTMAN` (case-insensitive, with surrounding whitespace ignored); otherwise it writes a `400 Bad Request` ProblemDetail response and stops request handling. This check defines an API client contract and does not authenticate or authorize callers.

After the interceptor permits the request, Spring MVC invokes the matching controller method. Create and update bodies are validated before the controller delegates to the service. The service uses the repository to load or persist `ItemEntity` instances and returns `Item` records; Spring serializes the response as JSON. Missing IDs raise `ItemNotFoundException` and are converted by the advice. The `/actuator/health` endpoint is not covered by the client-type interceptor but does receive the response security headers.

The `SecurityHeadersFilter` runs with highest precedence and sets:

- `X-Content-Type-Options: nosniff`
- `Cross-Origin-Resource-Policy: same-origin`
- `Content-Security-Policy: default-src 'none'; frame-ancestors 'none'`
- `X-Frame-Options: DENY`
- `Referrer-Policy: no-referrer`
- `Cache-Control: no-store`

Spring Security is not part of this application; the filter only adds response headers.

## API routes

| Method | Path | Behavior |
| --- | --- | --- |
| `GET` | `/api/items` | Returns all items ordered by ID |
| `GET` | `/api/items/{id}` | Returns one item or a `404` ProblemDetail |
| `POST` | `/api/items` | Creates an item; returns `201` and `Location` |
| `PUT` | `/api/items/{id}` | Replaces an item or returns a `404` ProblemDetail |
| `DELETE` | `/api/items/{id}` | Deletes an item; returns `204` or a `404` ProblemDetail |
| `GET` | `/actuator/health` | Returns Actuator health without the client-type header |

## Persistence and profiles

Shared JPA and server settings in `application.properties` set port `8282`, expose only the Actuator `health` endpoint, use `spring.jpa.hibernate.ddl-auto=update`, and disable Open EntityManager in View.

| Profile | Database | Configuration |
| --- | --- | --- |
| `local` (default) | H2 in-memory database `itemsdb` | No external database required; data is ephemeral |
| `dev` | PostgreSQL | Defaults to `jdbc:postgresql://localhost:5432/itemsdb`, username `postgres` |
| `mysql` | MySQL | Defaults to `jdbc:mysql://localhost:3306/testdb`, username `testuser` |

The PostgreSQL and MySQL profiles allow `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variable overrides. Database credentials should be provided through the environment, not committed to the repository.

## Build, image, and tests

Maven builds the application. `mvn test` runs the test suite; `ItemControllerTest` exercises the CRUD API, client-type contract, validation, response headers, and health endpoint using the `local` profile.

The `Dockerfile` packages the executable JAR in an Eclipse Temurin 21 JRE Alpine image, runs it as a non-root user, exposes port `8282`, and checks `/actuator/health` for container health.

An optional Maven `security` profile configures JaCoCo coverage, SpotBugs with Find Security Bugs, and OWASP Dependency-Check. SonarQube analysis can be run separately and requires a reachable server and `SONAR_TOKEN`; see `pom.xml` for commands and report locations.

## Design boundaries

The API DTOs are separate from the JPA entity, and controllers depend on the service interface rather than the repository. Transaction management is kept in the service implementation; database selection is controlled by Spring profiles. These boundaries keep HTTP, business operations, and persistence concerns distinct without introducing additional framework layers.
