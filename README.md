# Spring Boot REST Demo

A REST API for managing items, built with Spring Boot and Java. Items are stored
in memory and are cleared when the application restarts.

## Technology and versions

- Java 21
- Spring Boot 3.5.7 (Spring Framework 6.2.x, managed by Spring Boot)
- Maven 3.6 or newer

## Run

```shell
mvn spring-boot:run
```

The application listens on port `8282`.

## REST endpoints

Base URL: `http://localhost:8282`

| Method | Path | Description | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/items` | List all items | `200 OK` |
| `GET` | `/api/items/{id}` | Get an item by ID | `200 OK` |
| `POST` | `/api/items` | Create an item | `201 Created` with a `Location` header |
| `PUT` | `/api/items/{id}` | Replace an item | `200 OK` |
| `DELETE` | `/api/items/{id}` | Delete an item | `204 No Content` |
| `GET` | `/actuator/health` | Check application health (Spring Boot Actuator) | `200 OK` |

Every `/api/**` request must include the `X-Client-Type` header with the value
`UI` or `POSTMAN` (case-insensitive). Requests missing the header or using any
other value return `400 Bad Request`. This requirement does not apply to the
Actuator health endpoint.

Create and update requests require a non-blank `name` and accept an optional
`description`:

```json
{
  "name": "Example",
  "description": "An example item"
}
```

For example, create an item with:

```shell
curl -i -X POST http://localhost:8282/api/items \
  -H "X-Client-Type: POSTMAN" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Example\",\"description\":\"An example item\"}"
```

Unknown item IDs return `404 Not Found`. Invalid request bodies return
`400 Bad Request` with validation details. Only the Actuator health endpoint is
exposed over HTTP.

## Test

```shell
mvn test
```
