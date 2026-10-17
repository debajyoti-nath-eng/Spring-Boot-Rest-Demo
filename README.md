# Spring Boot REST Demo

A Spring Boot REST API for managing items. Items are stored in memory and are
cleared when the application restarts.

## Requirements

- Java 17 or newer
- Maven 3.6 or newer

## Run

```shell
mvn spring-boot:run
```

The API is available at `http://localhost:8282/api/items`.
The Actuator health endpoint is available at `http://localhost:8282/actuator/health`.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `GET` | `/api/items` | List all items | `200 OK` |
| `GET` | `/api/items/{id}` | Get an item | `200 OK` |
| `POST` | `/api/items` | Create an item | `201 Created` |
| `PUT` | `/api/items/{id}` | Replace an item | `200 OK` |
| `DELETE` | `/api/items/{id}` | Delete an item | `204 No Content` |
| `GET` | `/actuator/health` | Check application health | `200 OK` |

Create and update requests use JSON with a required, non-blank `name` and an
optional `description`:

```json
{
  "name": "Example",
  "description": "An example item"
}
```

Unknown item IDs return `404 Not Found`; invalid request bodies return
`400 Bad Request` with validation details.

## Test

```shell
mvn test
```
