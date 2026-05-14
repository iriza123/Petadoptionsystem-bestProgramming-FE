# Pet Adoption System

A full-stack web application for managing pet adoption processes.

## Technologies

**Backend:** Java 21, Spring Boot 4.0, Maven, PostgreSQL
**Frontend:** React 18, Vite
**Design Patterns:** MVC, Repository, Service Layer

## Quick Start

### Backend
```bash
cd backend
mvn spring-boot:run
```
Runs on: `http://localhost:8082`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Runs on: `http://localhost:5173`

### Database Setup
```bash
# Create PostgreSQL database
psql postgres
CREATE DATABASE petadoptiondb;
GRANT ALL PRIVILEGES ON DATABASE petadoptiondb TO nella;
\q
```

## Default Accounts

**Admin:** admin@shelter.com / admin123
**Test User:** Register at `/register`

## Project Structure

```
PetAdoptionSystem/
├── backend/          # Spring Boot API
│   ├── src/main/java/com/petadoption/system/
│   │   ├── model/        # Entities
│   │   ├── repository/   # Data access
│   │   ├── service/      # Business logic
│   │   └── controller/   # REST APIs
│   └── pom.xml
├── frontend/         # React app
│   └── src/
└── diagrams/        # UML diagrams
```

## Features

- Browse and filter pets
- Submit adoption requests
- Admin dashboard for managing pets
- Approve/reject adoption requests
- Notification system

## API Endpoints

**Auth:** `POST /api/auth/register`, `POST /api/auth/login`
**Pets:** `GET /api/pets`, `POST /api/pets` (admin)
**Adoptions:** `POST /api/adoptions`, `PUT /api/adoptions/{id}/approve` (admin)

Full API docs: `backend/API_ENDPOINTS.md`

## Docker Deployment

```bash
docker-compose up --build
```

## Build for Production

```bash
# Backend
cd backend && mvn clean package

# Frontend
cd frontend && npm run build
```

## Testing

**42 Unit Tests:** Covering all 4 service classes (UserService, PetService, AdoptionService, NotificationService)

```bash
cd backend
mvn test
```

Expected output:
```
Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Full test plan: `SOFTWARE_TEST_PLAN.md`

## Design Patterns

Three patterns applied — see `DESIGN_PATTERNS.md` for full documentation:
- **MVC Pattern** — separates Model, View, Controller layers
- **Repository Pattern** — separates data access from business logic
- **Service Layer Pattern** — isolates all business rules in service classes

## Project Requirements

✅ Topic and case study — Dog Haus, Kimironko, Kigali, Rwanda
✅ Functional Diagram — internal working of Dog Haus
✅ Problem statement — 8 problems identified
✅ Use Case Diagram — diagrams/use-case-pet-adoption-system.png
✅ Class Diagram — diagrams/class-diagram.png
✅ Activity Diagram — diagrams/Activity diagram.png
✅ Sequence Diagram — diagrams/sequence-diagram...png
✅ Component Diagram — diagrams/component diagram.jpg
✅ Software prototype — full-stack React + Spring Boot
✅ Google coding standards — Java Style Guide + JS Style Guide
✅ Design patterns — MVC + Repository + Service Layer
✅ Dockerization — multi-stage Dockerfiles + docker-compose
✅ Version control — Git initialized, committed to GitHub
✅ Software test plan — SOFTWARE_TEST_PLAN.md
✅ Unit tests — 42 tests with JUnit 5 + Mockito

---

**Author:** Nella
**Case Study:** Dog Haus — KG 4 St, Kimironko, Kigali, Rwanda
**Course:** Best Programming Practices and Design Patterns
**Academic Year:** 2025/2026
