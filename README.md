# Java REST API Project

This repository contains a small, self-contained CRUD REST API built with Java and Spring Boot. It provides a simple user management API that demonstrates best practices for a lightweight backend service: clear request/response formats, validation, error handling, duplicate prevention, and an in-memory H2 database for easy local testing.

This README is both a quick reference and a short manual that will help you set up, run, and understand the project.

**Contents**
- **Overview** — what the project does and why it exists
- **Technology stack** — languages, frameworks and versions used
- **Prerequisites** — what you need locally (Java 21, Maven)
- **Setup & Run** — step-by-step commands to run the app locally
- **API Reference** — endpoints, sample requests and responses
- **Data model** — main entity fields and constraints
- **Error handling** — how errors and validations are reported
- **Development notes** — important implementation details
- **Troubleshooting & FAQs** — common problems and fixes
- **Contributing** — how to help or extend the project

**Overview**

This small REST service exposes endpoints to create, read, update and delete `User` records. It is intended as a minimal but production-minded example with:
- Parameter name preservation (compiled with `-parameters`) so Spring can resolve method parameter names correctly
- Unique constraint on `email` to prevent duplicates
- Centralized exception handling and consistent JSON error responses
- H2 in-memory database for local testing

**Technology Stack**
- Java 21 (LTS) — required runtime
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- H2 in-memory database (runtime)
- Maven for build and dependency management
- Lombok (provided)

**Prerequisites**
- Java 21 installed and available on `PATH` (check with `java -version`)
- Maven installed (check with `mvn -v`)

If you don't have Java 21, install it from a trusted distribution (Adoptium / Eclipse Temurin, SDKMAN, or vendor of your choice).

**Setup & Run (local)**

1. Clone the repo (or ensure you're in project root where `pom.xml` is located).
2. Build the project:

```powershell
mvn clean install
```

3. Run the application:

```powershell
mvn spring-boot:run
```

By default the application runs on port `8080`.

H2 console is available at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `SA`).

**Project Structure (important files)**
- `pom.xml` — build configuration (Java 21, `maven-compiler-plugin` with `-parameters`)
- `src/main/java/com/mohan/api/controller/UserController.java` — REST endpoints
- `src/main/java/com/mohan/api/service/UserService.java` — business logic and validation
- `src/main/java/com/mohan/api/model/User.java` — JPA entity (unique `email`)
- `src/main/java/com/mohan/api/repository/UserRepository.java` — Spring Data JPA repository
- `src/main/java/com/mohan/api/exception` — custom exceptions and `GlobalExceptionHandler`

**API Reference**

Base path: `/users`

- `GET /users`
  - Description: Return all users.
  - Responses:
    - `200` — JSON array of users
    - `404` — `{ "message": "No users found in the database", "status": 404, "timestamp": <ms> }`

- `GET /users/{id}`
  - Description: Return a single user by `id`.
  - Responses:
    - `200` — JSON user object
    - `404` — `{ "message": "User not found with id: {id}", "status": 404, "timestamp": <ms> }`

- `POST /users`
  - Description: Create a new user.
  - Request body JSON:
    ```json
    {
      "name": "Alice",
      "email": "alice@example.com"
    }
    ```
  - Responses:
    - `201` — created user JSON
    - `400` — `{ "message": "User name and email cannot be empty", "status": 400, "timestamp": <ms> }` (validation)
    - `409` — `{ "message": "User with email 'x' already exists", "status": 409, "timestamp": <ms> }` (duplicate)

- `PUT /users/{id}`
  - Description: Update an existing user. Validates existence and duplicate email.
  - Responses:
    - `200` — updated user JSON
    - `400` — validation errors
    - `404` — user not found
    - `409` — duplicate email conflict

- `DELETE /users/{id}`
  - Description: Delete user by id.
  - Responses:
    - `200` — `{ "message": "User with id {id} deleted successfully" }`
    - `404` — user not found

**Sample curl requests**

Create user:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

Get user:
```bash
curl http://localhost:8080/users/1
```

Update user:
```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice B","email":"alice.b@example.com"}'
```

Delete user:
```bash
curl -X DELETE http://localhost:8080/users/1
```

**Data Model**

`User` fields (in `src/main/java/com/mohan/api/model/User.java`):
- `id` (Long) — auto-generated primary key
- `name` (String)
- `email` (String) — **unique** and not null (duplicate prevention enforced by a DB unique constraint and service checks)

**Error Handling**

All errors are returned as a JSON `ErrorResponse` object with fields `message`, `status`, and `timestamp`. The app uses a centralized `GlobalExceptionHandler` to translate exceptions into HTTP responses. Duplicate attempts yield HTTP `409 Conflict`.

Example error:
```json
{
  "message": "User not found with id: 1",
  "status": 404,
  "timestamp": 1764287798555
}
```

**Development Notes & Design Choices**

- The compiler is configured with `-parameters` (see `pom.xml`) so that parameter names are preserved for Spring's reflection.
- Duplicate prevention is implemented at two levels:
  1. A DB-level unique constraint on `users.email` to ensure data integrity.
  2. Service-level checks in `UserService` that throw `DuplicateUserException` with a descriptive message, which maps to HTTP `409`.
- The in-memory H2 DB makes local testing quick; change to a persistent DB by updating `application.properties` and dependencies.

**Testing**

No unit tests are included by default in this repo; you can add tests under `src/test/java`. To run a full build and (future) tests:

```powershell
mvn clean install
```

**Troubleshooting & FAQs**

- Q: I see "Name for argument of type [java.lang.Long] not specified" — what does that mean?
  - A: The compiler must preserve parameter names. The project `pom.xml` enables `-parameters` for the compiler. Make sure you're using the same JDK used to build the project (Java 21).

- Q: I get duplicate key or constraint violation when creating a user.
  - A: The `email` column has a unique constraint. If two requests attempt the same email concurrently, you may see a DB constraint error — the application checks and returns 409 Conflict when duplicates are detected.

**Contributing**

Contributions are welcome. Typical steps:
1. Fork the repo and create a feature branch
2. Add tests for new behavior
3. Submit a pull request with a clear description of changes

**License**

This project is released under the MIT license (or change to your preferred license).

---

If you'd like, I can also add a short `curl` script under `scripts/` to run common API scenarios, or add OpenAPI/Swagger documentation endpoints.
# Java REST API Project

A simple CRUD REST API built using **Java + Spring Boot**.
This project is created for freelancing portfolio and automation testing practice.

## Features
- Create, Update, Delete users
- H2 in-memory database
- Swagger documentation
- Clean folder structure

## Run the project
```
mvn spring-boot:run
```

Open Swagger:
```
http://localhost:8080/swagger-ui.html
