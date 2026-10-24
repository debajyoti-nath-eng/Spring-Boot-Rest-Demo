# Spring Boot REST Demo

This project is a Spring Boot 3 application that exposes a small REST API for managing `Item` resources. It demonstrates a clean layered architecture using Spring MVC, Spring Data JPA, Bean Validation, and profile-based database configuration.

The application is intentionally lightweight and suitable for learning, testing, and extending. The default profile runs locally with an embedded H2 database, while `dev` and `mysql` profiles target PostgreSQL and MySQL respectively.

## Overview

- Java 21
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Maven build

## Architecture at a glance

The application follows a common layered MVC + persistence structure:

- `controller`: REST endpoints for HTTP traffic
- `service`: business logic and transaction boundaries
- `repository`: Spring Data JPA persistence access
- `entity`: JPA-managed persistence model
- `dto`: transport/request models used by the API
- `exception`: API error modeling and exception mapping
- `config`: MVC and interceptor configuration

For the full design and runtime flow, see [ARCHITECTURE.md](ARCHITECTURE.md).

## Project structure

```text
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── SpringBootRestDemoApplication.java
│   │   └── package-info.java
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties
│       ├── application-dev.properties
│       └── application-mysql.properties
└── test/java/com/example/demo/controller/
```

## Runtime profiles

### local (default)

Runs with an in-memory H2 database and no local database setup required.

```bash
mvn spring-boot:run
```

To run explicitly:

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### dev (PostgreSQL)

Uses PostgreSQL for persistent storage.

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/itemsdb"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Defaults:
- `DB_URL`: `jdbc:postgresql://localhost:5432/itemsdb`
- `DB_USERNAME`: `postgres`

### mysql

Uses MySQL for persistent storage.

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/testdb"
$env:DB_USERNAME = "testuser"
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Defaults:
- `DB_URL`: `jdbc:mysql://localhost:3306/testdb`
- `DB_USERNAME`: `testuser`

## REST API

Base URL: `http://localhost:8282`

| Method | Path | Description | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/items` | List all items | `200 OK` |
| `GET` | `/api/items/{id}` | Retrieve an item by ID | `200 OK` |
| `POST` | `/api/items` | Create a new item | `201 Created` with `Location` header |
| `PUT` | `/api/items/{id}` | Replace an item | `200 OK` |
| `DELETE` | `/api/items/{id}` | Delete an item | `204 No Content` |
| `GET` | `/actuator/health` | Application health status | `200 OK` |

### Request contract

All `/api/**` requests require the `X-Client-Type` header. Supported values are:
- `UI`
- `POSTMAN`

The check is case-insensitive. If the header is missing or invalid, the application responds with `400 Bad Request`.

Create and update requests use this JSON body:

```json
{
  "name": "Example",
  "description": "An example item"
}
```

Rules:
- `name` is required and cannot be blank
- `description` is optional

Example create request:

```bash
curl -i -X POST http://localhost:8282/api/items \
  -H "X-Client-Type: POSTMAN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Example","description":"An example item"}'
```

### Error handling

- Missing item ID: `404 Not Found`
- Validation failure: `400 Bad Request` with field details
- Invalid client header: `400 Bad Request`

## Database and persistence notes

- Hibernate auto-updates the schema via `spring.jpa.hibernate.ddl-auto=update`
- `ItemEntity` is mapped to the `items` table
- Repository access is implemented through `JpaRepository<ItemEntity, Long>`
- Transactions are managed at the service layer

## Testing

Run the unit and integration tests with:

```bash
mvn test
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the complete license text.
