# Backend - Pet Adoption System

Spring Boot REST API for pet adoption management.

## Tech Stack
- Java 21, Spring Boot 4.0, Maven
- PostgreSQL database
- Spring Data JPA, Lombok

## Run
```bash
mvn spring-boot:run
```
Server: `http://localhost:8082`

## Database
PostgreSQL connection:
- Database: `petadoptiondb`
- Username: `nella`
- Config: `src/main/resources/application.properties`

## Structure
```
src/main/java/com/petadoption/system/
├── model/       # Entities (User, Pet, AdoptionRequest, Notification)
├── repository/  # JPA Repositories
├── service/     # Business Logic
├── controller/  # REST APIs
├── dto/         # Data Transfer Objects
└── exception/   # Error Handling
```

## API Documentation
See [API_ENDPOINTS.md](API_ENDPOINTS.md)

## Testing
```bash
mvn test
```
34 unit tests covering service, controller, and repository layers.
