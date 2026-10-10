# Learning Portal Backend

A robust REST API backend for an online learning platform, built with Java, Spring Boot, Spring Data JPA, Hibernate, and PostgreSQL.

## Tech Stack
- **Language**: Java 17 (Downgraded from 21 due to environment limitation)
- **Framework**: Spring Boot 3.3.x
- **Database**: PostgreSQL
- **Migrations**: Flyway
- **Security**: Spring Security with JWT Token-based Authentication
- **Documentation**: Springdoc OpenAPI (Swagger UI)

## Roles
1. **STUDENT**: Discover courses, enroll, track progress.
2. **TEACHER**: Create draft courses, add lessons, submit for review.
3. **ADMIN**: Approve/Reject courses, manage categories and users.

## Local Setup

### Prerequisites
- JDK 17
- Maven
- Docker & Docker Compose (for local database)

### Environment Configuration
Copy `.env.example` to `.env` or set the variables manually:
```bash
cp .env.example .env
```

### Database
Start the local PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

### Running the Application
Use the Maven Wrapper or Maven directly:
```bash
./mvnw spring-boot:run
```
Or:
```bash
mvn spring-boot:run
```
Flyway will automatically create the database schema on startup.

### API Documentation
Once the application is running, access the Swagger UI documentation at:
`http://localhost:8080/swagger-ui.html`

## Testing
Run the test suite using Testcontainers (Requires Docker):
```bash
mvn test
```

## Key Workflows
1. **Registration**: Students and Teachers can register via `/api/v1/auth/register`. Admins must be provisioned manually in the database.
2. **Course Creation**: Teachers create drafts, add lessons (VIDEO or TEXT), and submit via `/api/v1/teacher/courses/{id}/submit`.
3. **Approval**: Admins review and approve via `/api/v1/admin/courses/{id}/approve`.
4. **Enrollment**: Students enroll in published courses via `/api/v1/courses/{courseCode}/enroll`.
5. **Progress**: Students track progress via `/api/v1/enrollments/{id}/lessons/{lessonId}/progress`.

## Security Notes
- Passwords are encrypted using BCrypt.
- All secure endpoints require a valid JWT Bearer token.
- `ADMIN` role cannot be self-registered via public APIs.
# Bharat-Seba-Backend
