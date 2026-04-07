# Task Manager - Backend

Spring Boot 3.2 REST API for task management with PostgreSQL and Flyway migrations.

---

## Features

- RESTful API with versioning (`/api/v1`)
- PostgreSQL 16 database with Flyway migrations
- JPA/Hibernate for data persistence
- Input validation with Jakarta Validation
- CORS configuration for frontend access
- Pagination support for scalable data retrieval
- Comprehensive error handling

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Flyway** (database migrations)
- **Maven** (build tool)
- **JUnit 5 & Mockito** (testing)

---

## API Documentation

See [API.md](./API.md) for complete endpoint documentation.

---

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 16 (or Docker)

### 1. Database Setup

**Using Docker (Recommended):**

```bash
docker-compose up -d
```

This starts PostgreSQL on `localhost:5432` with:
- Database: `taskmanager`
- Username: `postgres`
- Password: `password`

**Manual Setup:**

```bash
# Create database
createdb taskmanager -U postgres

# Optional: Set environment variables
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

### 2. Build and Run

```bash
# Clean compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

The API will be available at: **http://localhost:8080/api/v1**

---

## Configuration

Application configuration is in `src/main/resources/application.yml`.

### Key Properties

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: validate  # Use 'validate' in production
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Environment Variables

- `DB_USERNAME` - PostgreSQL username (default: `postgres`)
- `DB_PASSWORD` - PostgreSQL password (default: `password`)

---

## Database Migrations

Flyway manages all schema changes. Migration files are in:

```
src/main/resources/db/migration/
```

Current migration:
- **V1__init.sql** - Creates `tasks` table with indexes and constraints

To add a new migration, create a file like `V2__add_new_column.sql` following Flyway's naming convention.

---

## Testing

Run all tests:

```bash
mvn test
```

Tests use H2 in-memory database (configured in `src/test/resources/application.yml`) for fast execution.

---

## Project Structure

```
src/main/java/com/example/taskmanager/
├── config/
│   └── CorsConfig.java          # CORS configuration
├── controller/
│   └── TaskController.java      # REST API endpoints
├── dto/
│   ├── CreateTaskRequest.java
│   ├── PaginatedResponse.java
│   ├── TaskDTO.java
│   └── UpdateTaskStatusRequest.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   └── ValidationErrorResponse.java
├── model/
│   └── Task.java                # JPA entity
├── repository/
│   └── TaskRepository.java      # Spring Data JPA repository
├── service/
│   ├── TaskService.java         # Service interface
│   └── TaskServiceImpl.java     # Service implementation
└── status/
    └── TaskStatus.java          # Status enum

src/main/resources/
├── application.yml              # Main configuration
└── db/migration/
    └── V1__init.sql             # Database schema
```

---

## API Endpoints

All endpoints are prefixed with `/api/v1`:

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/tasks` | List tasks with pagination |
| `GET` | `/tasks/{id}` | Get single task |
| `POST` | `/tasks` | Create new task |
| `PATCH` | `/tasks/{id}/status` | Update task status |
| `DELETE` | `/tasks/{id}` | Delete task |

See [API.md](./API.md) for full details with examples.

---

## Development

### Running in Dev Mode with Hot Reload

Spring Boot DevTools is included. Changes to Java files will automatically trigger a restart.

```bash
mvn spring-boot:run
```

### Debug Mode

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Then attach debugger to port 5005.

---

## Building for Production

```bash
# Build executable JAR (skip tests for faster build)
mvn clean package -DskipTests

# Run the JAR
java -jar target/task-manager-1.0.0.jar
```

**Important:** For production, ensure:
- PostgreSQL is running and accessible
- Flyway has necessary permissions
- Set appropriate `DB_USERNAME` and `DB_PASSWORD` environment variables
- Consider setting `jpa.hibernate.ddl-auto: validate` (already set) or `none`
- Enable Spring Boot Actuator health checks
- Configure proper logging levels
- Set up reverse proxy (nginx/Apache) if needed

---

## Database Schema

### tasks table

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | BIGSERIAL | Primary Key |
| `title` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | NULLABLE |
| `status` | VARCHAR(50) | NOT NULL, CHECK (IN ('TO_DO','IN_PROGRESS','DONE')) |
| `due_date` | TIMESTAMP | NULLABLE |
| `created_at` | TIMESTAMP | NOT NULL |
| `updated_at` | TIMESTAMP | NOT NULL |

Indexes:
- `idx_tasks_status` on `status`
- `idx_tasks_created_at` on `created_at DESC`

---

## CORS

The API is configured to accept requests from:
- `http://localhost:3000`
- `http://127.0.0.1:3000`

To add more origins, edit `CorsConfig.java` or `application.yml`.

---

## Error Handling

The application uses a global exception handler (`GlobalExceptionHandler`) to return consistent error responses:

**Validation Error (400):**
```json
{
  "timestamp": "2026-04-07T10:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "fieldErrors": {
    "title": "Title is required"
  }
}
```

**Not Found (404):**
```json
{
  "timestamp": "2026-04-07T10:30:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999"
}
```

---

## License

Educational/Demonstration project.
