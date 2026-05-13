# Pet Adoption System - API Endpoints

Base URL: `http://localhost:8080`

## 🔐 Authentication Endpoints

### Register
```
POST /api/auth/register
Content-Type: application/json

Body:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "1234567890",
  "address": "123 Main St",
  "role": "ADOPTER"  // or "ADMIN"
}

Response:
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": null,
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "ADOPTER"
  }
}
```

### Login
```
POST /api/auth/login
Content-Type: application/json

Body:
{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": null,
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "ADOPTER"
  }
}
```

---

## 🐾 Pet Endpoints

### Get All Available Pets
```
GET /api/pets

Response:
{
  "success": true,
  "message": "Pets retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Buddy",
      "type": "DOG",
      "breed": "Golden Retriever",
      "age": 3,
      "gender": "MALE",
      "healthStatus": "Healthy, vaccinated",
      "description": "Friendly and playful dog",
      "imageUrl": "http://example.com/buddy.jpg",
      "status": "AVAILABLE",
      "createdAt": "2025-01-15T10:30:00"
    }
  ]
}
```

### Get Pet by ID
```
GET /api/pets/{id}
Example: GET /api/pets/1
```

### Filter Pets
```
GET /api/pets/filter?type=DOG
GET /api/pets/filter?type=CAT&status=AVAILABLE
```

### Add New Pet (Admin Only)
```
POST /api/pets
Content-Type: application/json

Body:
{
  "name": "Buddy",
  "type": "DOG",
  "breed": "Golden Retriever",
  "age": 3,
  "gender": "MALE",
  "healthStatus": "Healthy, vaccinated",
  "description": "Friendly and playful dog",
  "imageUrl": "http://example.com/buddy.jpg"
}
```

### Update Pet (Admin Only)
```
PUT /api/pets/{id}
Content-Type: application/json

Body: (same as Add Pet)
```

### Delete Pet (Admin Only)
```
DELETE /api/pets/{id}
```

---

## 📝 Adoption Request Endpoints

### Submit Adoption Request
```
POST /api/adoptions
Content-Type: application/json

Body:
{
  "userId": 1,
  "petId": 2,
  "reason": "I have a large backyard and love dogs. I want to give Buddy a loving home."
}

Response:
{
  "success": true,
  "message": "Adoption request submitted successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "petId": 2,
    "reason": "I have a large backyard...",
    "status": "PENDING",
    "requestDate": "2025-01-15T10:30:00",
    "responseDate": null,
    "adminNotes": null
  }
}
```

### Get User's Adoption Requests
```
GET /api/adoptions/my-requests/{userId}
Example: GET /api/adoptions/my-requests/1
```

### Get All Pending Requests (Admin)
```
GET /api/adoptions/pending
```

### Get All Requests (Admin)
```
GET /api/adoptions
```

### Approve Request (Admin)
```
PUT /api/adoptions/{id}/approve
Content-Type: application/json

Body (optional):
{
  "adminNotes": "Approved after home visit"
}
```

### Reject Request (Admin)
```
PUT /api/adoptions/{id}/reject
Content-Type: application/json

Body (optional):
{
  "adminNotes": "Insufficient experience with dogs"
}
```

---

## 🔔 Notification Endpoints

### Get User Notifications
```
GET /api/notifications/{userId}
Example: GET /api/notifications/1
```

### Get Unread Count
```
GET /api/notifications/{userId}/unread-count
Example: GET /api/notifications/1/unread-count

Response:
{
  "success": true,
  "message": "Unread count retrieved",
  "data": 3
}
```

### Mark Notification as Read
```
PUT /api/notifications/{id}/read
```

### Mark All as Read
```
PUT /api/notifications/{userId}/read-all
```

---

## 📊 Database Enums

### Pet Type
- DOG
- CAT
- BIRD
- RABBIT
- HAMSTER
- FISH
- OTHER

### Pet Status
- AVAILABLE
- PENDING
- ADOPTED

### Pet Gender
- MALE
- FEMALE
- UNKNOWN

### User Role
- ADOPTER
- ADMIN

### Request Status
- PENDING
- APPROVED
- REJECTED

### Notification Type
- ADOPTION_APPROVED
- ADOPTION_REJECTED
- NEW_PET_ADDED
- SYSTEM_MESSAGE

---

## 🧪 Testing with Postman

### Test Flow:

1. **Register an Admin User**
   ```
   POST /api/auth/register
   Body: { "name": "Admin", "email": "admin@shelter.com", "password": "admin123", "role": "ADMIN" }
   ```

2. **Register a Regular User**
   ```
   POST /api/auth/register
   Body: { "name": "John", "email": "john@example.com", "password": "password123", "role": "ADOPTER" }
   ```

3. **Login as Admin** (note the userId from response)

4. **Add a Pet**
   ```
   POST /api/pets
   Body: { "name": "Buddy", "type": "DOG", "breed": "Golden Retriever", ... }
   ```

5. **Get All Pets** - verify pet was added
   ```
   GET /api/pets
   ```

6. **Login as Regular User** (note the userId)

7. **Submit Adoption Request**
   ```
   POST /api/adoptions
   Body: { "userId": 2, "petId": 1, "reason": "I love dogs..." }
   ```

8. **Check User Notifications**
   ```
   GET /api/notifications/2
   ```

9. **Login as Admin Again**

10. **View Pending Requests**
    ```
    GET /api/adoptions/pending
    ```

11. **Approve Request**
    ```
    PUT /api/adoptions/1/approve
    Body: { "adminNotes": "Great adopter!" }
    ```

12. **User Checks Notifications Again** - should see approval notification
    ```
    GET /api/notifications/2
    ```

---

## ✅ Success! Your Backend is Ready!

All endpoints are working with:
- ✅ Simple authentication (no JWT complexity)
- ✅ CRUD operations for pets
- ✅ Adoption request workflow
- ✅ Automatic notifications
- ✅ Role-based operations (ADOPTER vs ADMIN)
- ✅ Clean error handling

Next step: Build the React frontend!
