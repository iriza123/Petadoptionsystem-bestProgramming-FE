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

**34 Unit Tests:** Service, Controller, Repository layers
Run tests: `cd backend && mvn test`

## Exam Requirements

✅ Real-life problem solution
✅ Clean code (Google standards)
✅ Version control (Git/GitHub)
✅ Design patterns (MVC + Repository)
✅ Dockerization
✅ Testing (34 unit tests)
✅ 3 UML diagrams (Use Case, Sequence, Data Flow)

---

**Author:** Nella
**Course:** Software Engineering
**Date:** December 2025
