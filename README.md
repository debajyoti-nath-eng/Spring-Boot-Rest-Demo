# Spring Boot REST Demo

This project is a Spring Boot application that exposes a REST API for managing `Item` resources. It uses Spring MVC, Spring Data JPA, Jakarta Bean Validation, and profile-based database configuration.

## Technology

- Java 21
- Spring Boot 3.5.16
- Spring MVC, Spring Data JPA, and Jakarta Validation
- Spring Boot Actuator
- Maven

The Maven build also pins selected dependency versions beyond the Spring Boot-managed versions; see the version properties and comments in `pom.xml`.

## Project structure

```text
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/       # MVC client-header interceptor and response security-header filter
│   │   ├── controller/   # Item REST endpoints
│   │   ├── dto/          # Item response and validated request records
│   │   ├── entity/       # JPA persistence model
│   │   ├── exception/    # Domain exception and ProblemDetail advice
│   │   ├── repository/   # Spring Data JPA repository
│   │   ├── service/      # Service contract and transactional implementation
│   │   └── SpringBootRestDemoApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties
│       ├── application-dev.properties
│       └── application-mysql.properties
└── test/java/com/example/demo/controller/
    └── ItemControllerTest.java
```

## Run the application

The default `local` profile uses an in-memory H2 database and needs no database setup:

```bash
mvn spring-boot:run
```

To select a profile explicitly, for example:

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

The server listens on port `8282`.

### PostgreSQL (`dev` profile)

The defaults use `jdbc:postgresql://localhost:5432/itemsdb` and username `postgres`. Override them as needed:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/itemsdb"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### MySQL (`mysql` profile)

The defaults use `jdbc:mysql://localhost:3306/testdb` and username `testuser`. Override them as needed:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/testdb"
$env:DB_USERNAME = "testuser"
$env:DB_PASSWORD = "<your-password>"
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Both database profiles read connection settings from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. The password is supplied through the environment; do not commit credentials.

## REST API

Base URL: `http://localhost:8282`

| Method | Path | Description | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/items` | List items ordered by ID | `200 OK` |
| `GET` | `/api/items/{id}` | Retrieve an item | `200 OK` |
| `POST` | `/api/items` | Create an item | `201 Created`, item body, and `Location` header |
| `PUT` | `/api/items/{id}` | Replace an existing item | `200 OK` |
| `DELETE` | `/api/items/{id}` | Delete an existing item | `204 No Content` |
| `GET` | `/actuator/health` | Application health | `200 OK` |

Every `/api/**` request must include the `X-Client-Type` header. Accepted values are `UI` and `POSTMAN`; matching ignores surrounding whitespace and letter case. A missing or unsupported value returns `400 Bad Request`. This is a request-contract check, not authentication or authorization. Actuator health does not require the header.

Create and replace requests accept:

```json
{
  "name": "Example",
  "description": "An example item"
}
```

`name` is required, must not be blank, and is limited to 255 characters. `description` is optional and is also limited to 255 characters. Validation errors return `400 Bad Request` as a ProblemDetail response with an `errors` object keyed by field.

Example:

```bash
curl -i -X POST http://localhost:8282/api/items \
  -H "X-Client-Type: POSTMAN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Example","description":"An example item"}'
```

Missing item IDs return `404 Not Found` as a ProblemDetail response. Responses, including rejected requests and Actuator responses, receive defensive headers: `X-Content-Type-Options`, `Cross-Origin-Resource-Policy`, `Content-Security-Policy`, `X-Frame-Options`, `Referrer-Policy`, and `Cache-Control`.

## Persistence

`ItemEntity` maps to the `items` table. `ItemRepository` extends `JpaRepository<ItemEntity, Long>`, while the service layer owns transactional CRUD operations and maps entities to the API `Item` record. Hibernate schema management is configured as `update`, and Open EntityManager in View is disabled.

## Container image

Build the executable JAR and image, then run it with Podman (or a Docker-compatible CLI):

```bash
mvn -DskipTests package
podman build -t spring-boot-rest-demo:local .
podman run --rm -p 8282:8282 spring-boot-rest-demo:local
```

The image runs the application as a non-root user and defines a health check against `/actuator/health`.

## Tests and optional security scans

Run the tests with:

```bash
mvn test
```

An optional Maven `security` profile configures JaCoCo, SpotBugs with Find Security Bugs, and OWASP Dependency-Check:

```bash
mvn -Psecurity verify
```

Reports are written under `target`. SonarQube analysis is optional and requires a local SonarQube server and `SONAR_TOKEN`; see the profile comments in `pom.xml`.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the complete license text.
