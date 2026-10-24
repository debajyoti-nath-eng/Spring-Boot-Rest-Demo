# Spring Boot REST Demo

A REST API for managing items, built with Spring Boot, Spring Data JPA, and
Java. The active Spring profile selects the database: the `local` profile uses
an in-memory H2 database, the `dev` profile connects to PostgreSQL, and the
`mysql` profile connects to MySQL.

## Technology and versions

- Java 21
- Spring Boot 3.5.7 (Spring Framework 6.2.x, managed by Spring Boot)
- Maven 3.6 or newer

## Run

### Local profile (H2)

The `local` profile is the default. It requires no database installation; its
in-memory data is cleared when the application stops.

```shell
mvn spring-boot:run
```

To select it explicitly:

```shell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### Dev profile (PostgreSQL)

Create a PostgreSQL database named `itemsdb`, then provide its connection
settings as environment variables before starting the application:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/itemsdb"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

`DB_URL` and `DB_USERNAME` default to `jdbc:postgresql://localhost:5432/itemsdb`
and `postgres`; `DB_PASSWORD` must be supplied. For a packaged JAR, activate
the profile with `java -jar target/spring-boot-rest-demo-0.0.1-SNAPSHOT.jar
--spring.profiles.active=dev`.

### MySQL profile

Create a MySQL database named `testdb` and a user with access to it, then
provide the password as an environment variable before starting the
application:

```powershell
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

`DB_URL` and `DB_USERNAME` default to `jdbc:mysql://localhost:3306/testdb` and
`testuser`; set them to use a different database or user. `DB_PASSWORD` must
be supplied.

The application listens on port `8282`.

## Package structure

Application components are organized by layer under `com.example.demo`:
`config`, `controller`, `dto`, `entity`, `exception`, `repository`, and
`service`. The `ItemService` interface is in `service`, and its implementation
is in `service.impl`.

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
exposed over HTTP. Hibernate updates the database schema automatically.

## Test

```shell
mvn test
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for
the full license text.
