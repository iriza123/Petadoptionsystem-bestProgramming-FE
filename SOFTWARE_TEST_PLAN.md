# Software Test Plan
## Dog Haus Pet Adoption System — Kimironko, Kigali, Rwanda

**Project:** Online Pet Adoption Management System  
**Case Study:** Dog Haus — KG 4 St, Kimironko, Kigali, Rwanda  
**Course:** Best Programming Practices and Design Patterns  
**Version:** 1.0  
**Date:** May 2026  

---

## 1. Introduction

### 1.1 Purpose
This Software Test Plan describes the testing strategy, scope, approach, resources, and schedule for the Dog Haus Pet Adoption System. It serves as a roadmap for all testing activities to ensure the system meets its functional and non-functional requirements before deployment.

### 1.2 Scope
The system under test is a full-stack web application consisting of:
- **Backend:** Spring Boot 4.0 REST API (Java 21)
- **Frontend:** React 18 Single Page Application (Vite)
- **Database:** H2 (development), PostgreSQL (production)
- **Containerization:** Docker + Docker Compose

### 1.3 Objectives
- Verify that all functional requirements are correctly implemented
- Ensure the system handles invalid inputs and edge cases gracefully
- Confirm that the adoption workflow (submit → review → approve/reject → notify) works end-to-end
- Validate that role-based access (ADOPTER vs ADMIN) is enforced correctly

---

## 2. Test Items

The following components will be tested:

| Component | Description |
|-----------|-------------|
| UserService | Registration, login, user retrieval, profile update |
| PetService | Add, update, delete, filter, and retrieve pets |
| AdoptionService | Submit, approve, reject adoption requests |
| NotificationService | Create, retrieve, mark as read notifications |
| AuthController | POST /api/auth/register, POST /api/auth/login |
| PetController | GET/POST/PUT/DELETE /api/pets |
| AdoptionController | POST/GET/PUT /api/adoptions |
| NotificationController | GET/PUT /api/notifications |

---

## 3. Features to Be Tested

### 3.1 Authentication
- User registration with valid data
- User registration with duplicate email (should fail)
- User login with correct credentials
- User login with wrong password (should fail)
- User login with non-existent email (should fail)

### 3.2 Pet Management
- Admin adds a new pet
- Admin updates an existing pet
- Admin deletes a pet
- Retrieve all available pets
- Filter pets by type (DOG, CAT, BIRD, etc.)
- Filter pets by type and status
- Get pet by ID
- Get pet with invalid ID (should return 404)

### 3.3 Adoption Request Workflow
- Adopter submits a request for an AVAILABLE pet
- Adopter submits a request for a non-AVAILABLE pet (should fail)
- Admin approves a PENDING request
- Admin rejects a PENDING request
- Admin tries to approve an already-processed request (should fail)
- Pet status updates correctly: AVAILABLE → PENDING → ADOPTED
- Pet status reverts to AVAILABLE when request is rejected

### 3.4 Notification System
- Notification is created when adoption request is submitted
- Notification is created when request is approved
- Notification is created when request is rejected
- User retrieves their notifications
- User marks a notification as read
- User marks all notifications as read
- Unread count is returned correctly

### 3.5 Role-Based Access
- ADOPTER cannot access admin endpoints
- ADMIN can access all endpoints
- Unauthenticated user is redirected to login

---

## 4. Features NOT to Be Tested

- JWT token authentication (not implemented — simple password comparison used)
- Email sending (not implemented in this prototype)
- Payment processing (out of scope)
- Mobile responsiveness (out of scope for this phase)

---

## 5. Test Approach

### 5.1 Unit Testing
**Tool:** JUnit 5 + Mockito  
**Scope:** Service layer classes  
**Method:** Each service method is tested in isolation using mocked repositories. Dependencies are replaced with Mockito mocks so tests run without a database.

**Test Classes:**
| Test Class | Service Tested | Number of Tests |
|------------|---------------|-----------------|
| UserServiceTest | UserService | 9 tests |
| PetServiceTest | PetService | 11 tests |
| AdoptionServiceTest | AdoptionService | 11 tests |
| NotificationServiceTest | NotificationService | 11 tests |
| **Total** | | **42 unit tests** |

### 5.2 Integration Testing
**Tool:** Spring Boot Test + H2 in-memory database  
**Scope:** Controller layer — full HTTP request/response cycle  
**Method:** Use `@SpringBootTest` and `MockMvc` to send HTTP requests to controllers and verify responses without starting a real server.

### 5.3 Manual Testing
**Tool:** Postman / Browser  
**Scope:** End-to-end user flows  
**Method:** Manually execute the full adoption workflow using the running application.

---

## 6. Test Cases

### 6.1 UserService Test Cases

| Test ID | Test Name | Input | Expected Result | Status |
|---------|-----------|-------|-----------------|--------|
| US-01 | Register new user | Valid name, email, password | AuthResponse with userId and role | Pass |
| US-02 | Register duplicate email | Existing email | BadRequestException: "Email already registered" | Pass |
| US-03 | Register admin role | role = ADMIN | AuthResponse with role = ADMIN | Pass |
| US-04 | Login valid credentials | Correct email + password | AuthResponse with userId | Pass |
| US-05 | Login wrong email | Non-existent email | BadRequestException: "Invalid email or password" | Pass |
| US-06 | Login wrong password | Correct email, wrong password | BadRequestException: "Invalid email or password" | Pass |
| US-07 | Get user by ID | Valid userId | User object | Pass |
| US-08 | Get user by invalid ID | Non-existent userId | ResourceNotFoundException | Pass |
| US-09 | Update user profile | New name, phone, address | Updated User object | Pass |

### 6.2 PetService Test Cases

| Test ID | Test Name | Input | Expected Result | Status |
|---------|-----------|-------|-----------------|--------|
| PS-01 | Add new pet | Valid pet data | Saved Pet object | Pass |
| PS-02 | Add pet with null status | Pet with no status | Status defaults to AVAILABLE | Pass |
| PS-03 | Get pet by valid ID | Existing petId | Pet object | Pass |
| PS-04 | Get pet by invalid ID | Non-existent petId | ResourceNotFoundException | Pass |
| PS-05 | Get all pets | — | List of all pets | Pass |
| PS-06 | Get available pets | — | List of AVAILABLE pets only | Pass |
| PS-07 | Filter by type DOG | type = DOG | List of dogs only | Pass |
| PS-08 | Filter by type and status | type = CAT, status = AVAILABLE | List of available cats | Pass |
| PS-09 | Update pet | Valid petId + new data | Updated Pet object | Pass |
| PS-10 | Update pet status to PENDING | petId, PENDING | Pet with status PENDING | Pass |
| PS-11 | Delete pet | Valid petId | No exception, delete called | Pass |

### 6.3 AdoptionService Test Cases

| Test ID | Test Name | Input | Expected Result | Status |
|---------|-----------|-------|-----------------|--------|
| AS-01 | Submit request for AVAILABLE pet | userId, petId, reason | AdoptionRequest with PENDING status | Pass |
| AS-02 | Submit request for ADOPTED pet | petId with ADOPTED status | BadRequestException | Pass |
| AS-03 | Submit request for PENDING pet | petId with PENDING status | BadRequestException | Pass |
| AS-04 | Approve PENDING request | requestId, adminNotes | Request APPROVED, pet ADOPTED | Pass |
| AS-05 | Approve already-approved request | requestId with APPROVED status | BadRequestException | Pass |
| AS-06 | Approve non-existent request | Invalid requestId | ResourceNotFoundException | Pass |
| AS-07 | Reject PENDING request | requestId, adminNotes | Request REJECTED, pet AVAILABLE | Pass |
| AS-08 | Reject already-rejected request | requestId with REJECTED status | BadRequestException | Pass |
| AS-09 | Get user requests | userId | List of user's requests | Pass |
| AS-10 | Get pending requests | — | List of PENDING requests only | Pass |
| AS-11 | Get all requests | — | All requests ordered by date | Pass |

### 6.4 NotificationService Test Cases

| Test ID | Test Name | Input | Expected Result | Status |
|---------|-----------|-------|-----------------|--------|
| NS-01 | Create SYSTEM_MESSAGE notification | userId, message, type | Saved Notification, isRead=false | Pass |
| NS-02 | Create ADOPTION_APPROVED notification | userId, message, type | Notification with ADOPTION_APPROVED type | Pass |
| NS-03 | Create ADOPTION_REJECTED notification | userId, message, type | Notification with ADOPTION_REJECTED type | Pass |
| NS-04 | Get all user notifications | userId | List of notifications newest first | Pass |
| NS-05 | Get notifications for user with none | Non-existent userId | Empty list | Pass |
| NS-06 | Get unread notifications | userId | Only unread notifications | Pass |
| NS-07 | Mark notification as read | notificationId | Notification with isRead=true | Pass |
| NS-08 | Mark non-existent notification as read | Invalid notificationId | ResourceNotFoundException | Pass |
| NS-09 | Mark all as read | userId with 2 unread | save() called twice | Pass |
| NS-10 | Mark all as read with no unread | userId with 0 unread | save() never called | Pass |
| NS-11 | Count unread notifications | userId | Correct count returned | Pass |

---

## 7. Test Environment

| Component | Details |
|-----------|---------|
| Operating System | Windows 11 |
| Java Version | Java 21 (Eclipse Temurin) |
| Build Tool | Maven 3.9 |
| Test Framework | JUnit 5 + Mockito |
| Database (Test) | H2 In-Memory |
| IDE | VS Code / IntelliJ IDEA |
| API Testing | Postman |
| Browser Testing | Google Chrome |

---

## 8. How to Run the Tests

```bash
# Navigate to backend directory
cd backend

# Run all unit tests
mvn test

# Run tests with detailed output
mvn test -Dsurefire.useFile=false

# Run a specific test class
mvn test -Dtest=UserServiceTest

# Run tests and generate report
mvn test surefire-report:report
```

Expected output:
```
Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 9. Test Schedule

| Phase | Activity | Duration |
|-------|----------|----------|
| 1 | Write unit tests for all 4 services | Week 1 |
| 2 | Run unit tests and fix failures | Week 1 |
| 3 | Manual API testing with Postman | Week 2 |
| 4 | End-to-end browser testing | Week 2 |
| 5 | Docker environment testing | Week 2 |

---

## 10. Roles and Responsibilities

| Role | Responsibility |
|------|---------------|
| Developer / Tester | Write and run all unit tests |
| Developer / Tester | Perform manual API testing with Postman |
| Developer / Tester | Perform end-to-end browser testing |

---

## 11. Entry and Exit Criteria

### Entry Criteria (when to start testing)
- All service classes are implemented
- Application builds successfully with `mvn clean package`
- H2 database initializes correctly on startup

### Exit Criteria (when testing is complete)
- All 42 unit tests pass with 0 failures
- All manual test cases in Section 6 are verified
- No critical bugs remain open
- Application runs successfully in Docker containers

---

## 12. Test Summary

| Category | Count |
|----------|-------|
| UserService tests | 9 |
| PetService tests | 11 |
| AdoptionService tests | 11 |
| NotificationService tests | 11 |
| **Total Unit Tests** | **42** |
| Manual API test cases | 20+ |
| End-to-end browser flows | 5 |

The test suite covers all critical business logic of the Dog Haus Pet Adoption System including the complete adoption workflow, error handling, role-based access, and notification delivery.
