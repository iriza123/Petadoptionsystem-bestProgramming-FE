# Design Patterns Used in Dog Haus Pet Adoption System

**Project:** Online Pet Adoption Management System  
**Case Study:** Dog Haus — KG 4 St, Kimironko, Kigali, Rwanda  
**Course:** Best Programming Practices and Design Patterns  

---

## Overview

Three software design patterns were applied during the design and development of the Dog Haus Pet Adoption System. Each pattern was chosen to solve a specific structural or behavioral problem in the application.

---

## 1. MVC Pattern (Model-View-Controller)

### What It Is
MVC is an architectural design pattern that separates an application into three interconnected components: Model (data), View (UI), and Controller (logic handler). This separation ensures that changes in one layer do not affect the others.

### How It Was Applied

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Model | `User`, `Pet`, `AdoptionRequest`, `Notification` | Represent data and database entities |
| View | React Frontend (Login, Home, Dashboard, etc.) | Render UI and display data to users |
| Controller | `AuthController`, `PetController`, `AdoptionController`, `NotificationController` | Handle HTTP requests and return responses |

### Code Example — Controller (HTTP Handler)
```java
// PetController.java
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    /**
     * Get all available pets
     * GET /api/pets
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Pet>>> getAllPets() {
        List<Pet> pets = petService.getAvailablePets();
        return ResponseEntity.ok(ApiResponse.success("Pets retrieved successfully", pets));
    }
}
```

### Code Example — Model (Data Entity)
```java
// Pet.java
@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private PetType type;
    private Status status = Status.AVAILABLE;
    // ...
}
```

### Code Example — View (React Frontend)
```jsx
// Home.jsx
function Home({ user }) {
  const [pets, setPets] = useState([])

  useEffect(() => {
    api.getAllPets().then(response => {
      if (response.success) setPets(response.data)
    })
  }, [])

  return (
    <div className="pets-grid">
      {pets.map(pet => (
        <div key={pet.id} className="pet-card">
          <h3>{pet.name}</h3>
          <Link to={`/pets/${pet.id}`}>View Details</Link>
        </div>
      ))}
    </div>
  )
}
```

### Benefit
The frontend (React) can be completely replaced without touching the backend. The backend API can be changed without affecting the database models. Each layer is independently maintainable.

---

## 2. Repository Pattern

### What It Is
The Repository Pattern separates the data access logic from the business logic. Instead of writing database queries directly in service classes, each entity has a dedicated repository interface that handles all database operations.

### How It Was Applied

Each of the 4 entities has its own dedicated repository:

```
UserRepository              → handles all User database operations
PetRepository               → handles all Pet database operations
AdoptionRequestRepository   → handles all AdoptionRequest operations
NotificationRepository      → handles all Notification operations
```

### Code Example — Repository Interface
```java
// PetRepository.java
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // Find all available pets ordered by newest first
    List<Pet> findByStatusOrderByCreatedAtDesc(Pet.Status status);

    // Filter pets by type
    List<Pet> findByType(Pet.PetType type);

    // Filter pets by type and status
    List<Pet> findByTypeAndStatus(Pet.PetType type, Pet.Status status);
}
```

```java
// AdoptionRequestRepository.java
@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    // Get all requests by a specific user
    List<AdoptionRequest> findByUserId(Long userId);

    // Get all requests with a specific status
    List<AdoptionRequest> findByStatus(AdoptionRequest.RequestStatus status);

    // Get all requests ordered by date (newest first)
    List<AdoptionRequest> findAllByOrderByRequestDateDesc();
}
```

### Code Example — Service Using Repository
```java
// PetService.java
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository; // Repository injected

    /**
     * Get all available pets for the homepage
     */
    public List<Pet> getAvailablePets() {
        // Business logic calls repository — no SQL written directly
        return petRepository.findByStatusOrderByCreatedAtDesc(Pet.Status.AVAILABLE);
    }

    /**
     * Add new pet (Admin only)
     */
    public Pet addPet(Pet pet) {
        if (pet.getStatus() == null) {
            pet.setStatus(Pet.Status.AVAILABLE);
        }
        return petRepository.save(pet);
    }
}
```

### Benefit
The service layer never writes SQL directly. If the database is switched from H2 to PostgreSQL (as done in this project), only the configuration changes — no service code changes. Tests can mock the repository without needing a real database.

---

## 3. Service Layer Pattern

### What It Is
The Service Layer Pattern isolates all business logic in dedicated service classes. Controllers only handle HTTP concerns (request/response), and repositories only handle data access. All business rules live exclusively in the service layer.

### How It Was Applied

```
UserService        → registration validation, login authentication, profile management
PetService         → pet CRUD, status transitions (AVAILABLE → PENDING → ADOPTED)
AdoptionService    → adoption workflow: submit → approve/reject → notify
NotificationService → notification creation, read/unread management
```

### Code Example — Complex Business Logic in Service
```java
// AdoptionService.java
@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetService petService;
    private final NotificationService notificationService;

    /**
     * Approve adoption request — orchestrates multiple operations
     */
    @Transactional
    public AdoptionRequest approveRequest(Long requestId, String adminNotes) {

        // 1. Get the request and validate it is still PENDING
        AdoptionRequest request = getRequestById(requestId);
        if (request.getStatus() != AdoptionRequest.RequestStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }

        // 2. Update request status to APPROVED
        request.setStatus(AdoptionRequest.RequestStatus.APPROVED);
        request.setResponseDate(LocalDateTime.now());
        request.setAdminNotes(adminNotes);
        AdoptionRequest updatedRequest = adoptionRequestRepository.save(request);

        // 3. Update pet status to ADOPTED
        petService.updatePetStatus(request.getPetId(), Pet.Status.ADOPTED);

        // 4. Send notification to the adopter
        notificationService.createNotification(
            request.getUserId(),
            "Congratulations! Your adoption request has been approved!",
            Notification.NotificationType.ADOPTION_APPROVED
        );

        return updatedRequest;
    }
}
```

### Benefit
The controller `AdoptionController` simply calls `adoptionService.approveRequest()` in one line. All the complex logic — validating status, updating the request, updating the pet, sending a notification — is handled inside the service. This makes the code easy to test, maintain, and extend.

---

## Summary

| Pattern | Where Applied | Problem It Solves |
|---------|--------------|-------------------|
| MVC | Entire application architecture | Separates UI, business logic, and data into independent layers |
| Repository | All 4 JPA repositories | Separates database queries from business logic |
| Service Layer | All 4 service classes | Isolates business rules from controllers and repositories |

These three patterns work together to give the Dog Haus system a clean, layered architecture where each class has exactly one responsibility — following the Single Responsibility Principle from Google Java Style Guide.
