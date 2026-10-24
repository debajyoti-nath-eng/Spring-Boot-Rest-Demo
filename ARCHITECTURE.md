# Architecture Documentation

## Purpose

This project demonstrates a compact, maintainable Spring Boot REST API for managing `Item` records. The implementation intentionally follows a layered design so the business rules, HTTP layer, and persistence concerns remain separate and easy to reason about.

## High-level architecture

The application is organized around a standard Spring Boot layered pattern:

- HTTP layer: `controller`
- Service layer: `service`
- Persistence layer: `repository`
- Domain model: `entity`
- DTO layer: `dto`
- Error handling: `exception`
- Cross-cutting config: `config`

The runtime starts from `SpringBootRestDemoApplication`, which bootstraps the Spring application context and activates the selected profile.

## Request flow

A typical request follows this path:

1. An HTTP request arrives at the application.
2. Spring MVC route matching selects a controller method.
3. `WebConfig` registers `ClientTypeHeaderInterceptor`, which validates the `X-Client-Type` header on `/api/**` routes.
4. The controller method validates request input using Jakarta Validation annotations.
5. The controller delegates to the service interface.
6. The service layer performs business logic and transaction boundaries.
7. The repository interacts with the database using Spring Data JPA.
8. The entity is mapped to `ItemEntity` and persisted to the configured database.
9. The service converts the entity back to a DTO and returns it to the controller.
10. The HTTP response is built and serialized to JSON.

## Component responsibilities

### Controller layer

Package: `com.example.demo.controller`

The controller exposes REST endpoints for the `Item` resource. This layer is responsible for:

- handling HTTP requests and mapping URLs
- validating incoming request bodies
- translating service results to HTTP responses
- generating resource URIs for creation responses

`ItemController` is the main API entry point and contains CRUD endpoints.

### Service layer

Package: `com.example.demo.service` and `com.example.demo.service.impl`

The service layer is the transactional boundary for the API. It owns business operations such as:

- listing all items
- retrieving one item by ID
- creating a new item
- updating an existing item
- deleting an item

`ItemServiceImpl` performs lookups using `ItemRepository` and wraps persistence behavior in transactions.

### Repository layer

Package: `com.example.demo.repository`

The repository layer is built with `JpaRepository<ItemEntity, Long>`. It provides CRUD methods and search capability without custom SQL boilerplate. It is intentionally thin and keeps persistence logic close to Spring Data conventions.

### Entity and DTO model

- `ItemEntity`: JPA persistence model representing an item stored in the database
- `Item`: API response model returned to clients
- `ItemRequest`: input model for create/update requests

This separation keeps database concerns out of the API contract and gives a clear distinction between persistence and transport models.

### Exception handling

Package: `com.example.demo.exception`

The application uses `@RestControllerAdvice` to translate domain exceptions and validation failures into structured `ProblemDetail` responses. This ensures that API clients receive consistent, machine-readable error payloads.

Key exceptions include:

- `ItemNotFoundException`: mapped to `404 Not Found`
- `MethodArgumentNotValidException`: mapped to `400 Bad Request`

## Security and request validation

The application includes an interceptor-based validation layer for API requests:

- `ClientTypeHeaderInterceptor` checks for `X-Client-Type`
- allowed values are `UI` and `POSTMAN`
- checks are case-insensitive
- invalid or missing header values are rejected before the controller executes

This adds a simple cross-cutting validation safeguard across all `/api/**` endpoints.

## Persistence configuration

The application uses profile-specific property files:

- `application-local.properties`: H2 in-memory database
- `application-dev.properties`: PostgreSQL configuration
- `application-mysql.properties`: MySQL configuration

The default active profile is `local` and is defined in `application.properties`.

```properties
server.port=8282
management.endpoints.web.exposure.include=health
management.endpoint.health.enabled=true
spring.profiles.default=local
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```

Spring Boot uses the active profile to select the correct datasource and driver.

## Dependency flow

The dependency relationship is simple and explicit:

```text
ItemController -> ItemService -> ItemRepository -> ItemEntity
                                  ^
                                  |
                              database configuration
```

This keeps dependencies moving inward and supports easy testing at each layer.

## Testing approach

The project includes controller tests to validate request handling and API behavior. The tests cover expected status codes and the contract shape for the item endpoints.

A typical verification command is:

```bash
mvn test
```

## Design strengths

- Minimal and readable service API
- Separation of concerns between transport and persistence models
- Profile-based environment support
- Consistent error handling through `ProblemDetail`
- Simple dependency graph for easy onboarding

## Potential extension areas

This project can be naturally extended with:

- pagination and sorting for large item collections
- audit fields such as created/updated timestamps
- authentication and authorization
- additional resource types beyond `Item`
- integration tests with Testcontainers
- API versioning and OpenAPI documentation

## Summary

The architecture is intentionally small but realistic. It demonstrates how a Spring Boot REST API can be organized into clear layers, validated consistently, and configured for multiple environments without adding unnecessary complexity.
